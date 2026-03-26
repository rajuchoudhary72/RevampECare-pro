package com.app.ecarepro.feature.discipline.screens.addappreciationscreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.discipline.AddAppreciationFormData
import com.app.ecarepro.core.domain.model.discipline.AppreciationReward
import com.app.ecarepro.core.domain.model.discipline.AppreciationType
import com.app.ecarepro.core.domain.model.discipline.DisciplineUserInfo
import com.app.ecarepro.core.domain.repository.DisciplineRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.discipline.navigation.DisciplineNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel(assistedFactory = AddAppreciationViewModel.Factory::class)
class AddAppreciationViewModel @AssistedInject constructor(
    @Assisted val navKey: DisciplineNavigationGraph.AddAppreciation,
    private val disciplineRepository: DisciplineRepository,
) : BaseViewModel<AddAppreciationIntent, AddAppreciationEvent>() {

    private val studentId = navKey.studentId

    private val _uiState: MutableStateFlow<UiState<AddAppreciationUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchFormData()
    }

    override fun handleIntent(intent: AddAppreciationIntent) {
        when (intent) {
            AddAppreciationIntent.OnBackClicked -> sendEvent(AddAppreciationEvent.NavigateBack)
            is AddAppreciationIntent.OnCategorySelected -> onCategorySelected(intent.category)
            is AddAppreciationIntent.OnSubcategorySelected -> onSubcategorySelected(intent.subcategory)
            is AddAppreciationIntent.OnRewardSelected -> onRewardSelected(intent.reward)
            is AddAppreciationIntent.OnRemarksChanged -> updateState { it.copy(remarks = intent.remarks) }
            AddAppreciationIntent.OnCategorySelectClicked -> updateState { it.copy(isCategorySheetVisible = true) }
            AddAppreciationIntent.OnSubcategorySelectClicked -> {
                val state = (_uiState.value as? UiState.Success)?.data ?: return
                if (state.selectedCategory == null) {
                    sendMessage("Please select a category first", MessageType.WARNING)
                } else {
                    updateState { it.copy(isSubcategorySheetVisible = true) }
                }
            }
            AddAppreciationIntent.OnRewardSelectClicked -> updateState { it.copy(isRewardSheetVisible = true) }
            AddAppreciationIntent.OnDismissCategorySheet -> updateState { it.copy(isCategorySheetVisible = false) }
            AddAppreciationIntent.OnDismissSubcategorySheet -> updateState { it.copy(isSubcategorySheetVisible = false) }
            AddAppreciationIntent.OnDismissRewardSheet -> updateState { it.copy(isRewardSheetVisible = false) }
            is AddAppreciationIntent.OnSaveClicked -> saveAppreciation(intent.action)
        }
    }

    private fun fetchFormData() {
        viewModelScope.launch {
            disciplineRepository.getAddAppreciationForm(studentId).collect { result ->
                result.onSuccess { formData ->
                    _uiState.update {
                        UiState.Success(
                            AddAppreciationUiState(
                                userInfo = formData.studentInfo,
                                categories = formData.appreciationTypes,
                                rewards = formData.appreciationRewards,
                            )
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { UiState.Error(error.errorMessage()) }
                }
            }
        }
    }

    private fun onCategorySelected(category: String) {
        val state = (_uiState.value as? UiState.Success)?.data ?: return
        val selectedType = state.categories.find { it.name == category }
        updateState {
            it.copy(
                isCategorySheetVisible = false,
                selectedCategory = selectedType,
                selectedSubcategory = null,
                subcategories = emptyList(),
                instanceCount = "0",
            )
        }
        if (selectedType != null) {
            fetchSubcategories(selectedType.id)
        }
    }

    private fun fetchSubcategories(aprID: Int) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            disciplineRepository.getSubAppreciationTypes(aprID).collect { result ->
                result.onSuccess { subTypes ->
                    updateState { it.copy(isLoading = false, subcategories = subTypes) }
                }.onFailure { error ->
                    updateState { it.copy(isLoading = false) }
                    sendMessage(error.errorMessage(), MessageType.ERROR)
                }
            }
        }
    }

    private fun onSubcategorySelected(subcategory: String) {
        val state = (_uiState.value as? UiState.Success)?.data ?: return
        val selectedSub = state.subcategories.find { it.name == subcategory }
        updateState {
            it.copy(
                isSubcategorySheetVisible = false,
                selectedSubcategory = selectedSub,
            )
        }
        if (selectedSub != null) {
            fetchInstance(selectedSub.id)
        }
    }

    private fun fetchInstance(aprSubID: Int) {
        viewModelScope.launch {
            disciplineRepository.getAppreciationInstance(aprSubID, studentId).collect { result ->
                result.onSuccess { instance ->
                    updateState { it.copy(instanceCount = instance) }
                }.onFailure { /* ignore */ }
            }
        }
    }

    private fun onRewardSelected(reward: String) {
        val state = (_uiState.value as? UiState.Success)?.data ?: return
        val selected = state.rewards.find { it.name == reward }
        updateState { it.copy(isRewardSheetVisible = false, selectedReward = selected) }
    }

    private fun saveAppreciation(action: Int) {
        viewModelScope.launch {
            val state = (_uiState.value as? UiState.Success)?.data ?: return@launch

            if (state.selectedSubcategory == null) {
                sendMessage("Please select a subcategory", MessageType.WARNING)
                return@launch
            }

            updateState { it.copy(isLoading = true) }

            val formatter = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault())
            val dateTime = formatter.format(Date())

            disciplineRepository.saveAppreciation(
                action = action,
                stID = studentId,
                aprSubID = state.selectedSubcategory.id,
                rwdID = state.selectedReward?.id,
                instance = state.instanceCount,
                appreciationOn = dateTime,
                remark = state.remarks.takeIf { it.isNotBlank() },
            ).collect { result ->
                updateState { it.copy(isLoading = false) }
                result.onSuccess { message ->
                    sendMessage(message, MessageType.SUCCESS)
                    sendEvent(AddAppreciationEvent.NavigateBack)
                }.onFailure { error ->
                    sendMessage(error.errorMessage(), MessageType.ERROR)
                }
            }
        }
    }

    private fun updateState(update: (AddAppreciationUiState) -> AddAppreciationUiState) {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) UiState.Success(update(currentState.data))
            else currentState
        }
    }

    private fun sendMessage(text: String, type: MessageType) {
        sendEvent(AddAppreciationEvent.ShowMessage(SnackbarMessage(text, type)))
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DisciplineNavigationGraph.AddAppreciation, AddAppreciationViewModel> {
        override fun create(param: DisciplineNavigationGraph.AddAppreciation): AddAppreciationViewModel
    }
}

@Immutable
data class AddAppreciationUiState(
    val isLoading: Boolean = false,
    val userInfo: DisciplineUserInfo? = null,
    val categories: List<AppreciationType> = emptyList(),
    val subcategories: List<AppreciationType> = emptyList(),
    val rewards: List<AppreciationReward> = emptyList(),
    val selectedCategory: AppreciationType? = null,
    val selectedSubcategory: AppreciationType? = null,
    val selectedReward: AppreciationReward? = null,
    val instanceCount: String = "0",
    val remarks: String = "",
    val isCategorySheetVisible: Boolean = false,
    val isSubcategorySheetVisible: Boolean = false,
    val isRewardSheetVisible: Boolean = false,
)

sealed interface AddAppreciationIntent {
    data object OnBackClicked : AddAppreciationIntent
    data class OnCategorySelected(val category: String) : AddAppreciationIntent
    data class OnSubcategorySelected(val subcategory: String) : AddAppreciationIntent
    data class OnRewardSelected(val reward: String) : AddAppreciationIntent
    data class OnRemarksChanged(val remarks: String) : AddAppreciationIntent
    data object OnCategorySelectClicked : AddAppreciationIntent
    data object OnSubcategorySelectClicked : AddAppreciationIntent
    data object OnRewardSelectClicked : AddAppreciationIntent
    data object OnDismissCategorySheet : AddAppreciationIntent
    data object OnDismissSubcategorySheet : AddAppreciationIntent
    data object OnDismissRewardSheet : AddAppreciationIntent
    data class OnSaveClicked(val action: Int) : AddAppreciationIntent
}

sealed interface AddAppreciationEvent {
    data object NavigateBack : AddAppreciationEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : AddAppreciationEvent
}
