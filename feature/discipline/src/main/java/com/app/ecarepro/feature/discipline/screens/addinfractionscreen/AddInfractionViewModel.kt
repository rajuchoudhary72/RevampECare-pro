package com.app.ecarepro.feature.discipline.screens.addinfractionscreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.discipline.AddInfractionFormData
import com.app.ecarepro.core.domain.model.discipline.DisciplineUserInfo
import com.app.ecarepro.core.domain.model.discipline.InfractionConsequence
import com.app.ecarepro.core.domain.model.discipline.InfractionType
import com.app.ecarepro.core.domain.repository.DisciplineRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SelectedFileDetails
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.common.Base64Utils
import com.app.ecarepro.feature.discipline.DisciplineUserType
import com.app.ecarepro.feature.discipline.navigation.DisciplineNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel(assistedFactory = AddInfractionViewModel.Factory::class)
class AddInfractionViewModel @AssistedInject constructor(
    @Assisted val navKey: DisciplineNavigationGraph.AddInfraction,
    private val disciplineRepository: DisciplineRepository,
) : BaseViewModel<AddInfractionIntent, AddInfractionEvent>() {

    private val userId = navKey.userId
    private val userType = navKey.userType

    private val _uiState: MutableStateFlow<UiState<AddInfractionUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchFormData()
    }

    override fun handleIntent(intent: AddInfractionIntent) {
        when (intent) {
            AddInfractionIntent.OnBackClicked -> sendEvent(AddInfractionEvent.NavigateBack)

            is AddInfractionIntent.OnCategorySelected -> onCategorySelected(intent.category)
            is AddInfractionIntent.OnSubcategorySelected -> onSubcategorySelected(intent.subcategory)
            is AddInfractionIntent.OnConsequenceSelected -> onConsequenceSelected(intent.consequence)
            is AddInfractionIntent.OnRemarksChanged -> updateState { it.copy(remarks = intent.remarks) }
            is AddInfractionIntent.OnComplianceToggled -> updateState { it.copy(isComplianceActive = intent.active) }
            is AddInfractionIntent.OnFileSelected -> updateState { it.copy(selectedFile = intent.file, isFileUploadSheetVisible = false) }
            AddInfractionIntent.OnDeleteFile -> updateState { it.copy(selectedFile = null) }
            AddInfractionIntent.OnAddFileClicked -> updateState { it.copy(isFileUploadSheetVisible = true) }
            AddInfractionIntent.OnDismissFileUploadSheet -> updateState { it.copy(isFileUploadSheetVisible = false) }

            AddInfractionIntent.OnCategorySelectClicked -> updateState { it.copy(isCategorySheetVisible = true) }
            AddInfractionIntent.OnSubcategorySelectClicked -> {
                val state = (_uiState.value as? UiState.Success)?.data ?: return
                if (state.selectedCategory == null) {
                    sendMessage("Please select a category first", MessageType.WARNING)
                } else {
                    updateState { it.copy(isSubcategorySheetVisible = true) }
                }
            }
            AddInfractionIntent.OnConsequenceSelectClicked -> updateState { it.copy(isConsequenceSheetVisible = true) }
            AddInfractionIntent.OnDismissCategorySheet -> updateState { it.copy(isCategorySheetVisible = false) }
            AddInfractionIntent.OnDismissSubcategorySheet -> updateState { it.copy(isSubcategorySheetVisible = false) }
            AddInfractionIntent.OnDismissConsequenceSheet -> updateState { it.copy(isConsequenceSheetVisible = false) }

            is AddInfractionIntent.OnSaveClicked -> saveInfraction(intent.action)
            is AddInfractionIntent.OnShowError -> sendMessage(intent.error, MessageType.ERROR)
        }
    }

    private fun fetchFormData() {
        viewModelScope.launch {
            val flow = if (userType == DisciplineUserType.STUDENT) {
                disciplineRepository.getAddInfractionForm(userId)
            } else {
                disciplineRepository.getAddStaffInfractionForm(userId)
            }

            flow.collect { result ->
                result.onSuccess { formData ->
                    _uiState.update {
                        UiState.Success(
                            AddInfractionUiState(
                                userInfo = formData.studentInfo ?: formData.staffInfo,
                                categories = formData.infractionTypes,
                                consequences = formData.infractionConsequences,
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

    private fun fetchSubcategories(infrTypeID: Int) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            disciplineRepository.getSubInfractionTypes(infrTypeID).collect { result ->
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
        if (selectedSub != null && state.selectedCategory != null) {
            fetchInstance(state.selectedCategory.id, selectedSub.id)
        }
    }

    private fun fetchInstance(infrTypeID: Int, subTypeID: Int) {
        viewModelScope.launch {
            val params = mutableMapOf<String, Any>(
                "InfrTypeID" to infrTypeID,
                "InfrSubTypeID" to subTypeID,
            )
            if (userType == DisciplineUserType.STUDENT) {
                params["StID"] = userId
            } else {
                params["SID"] = userId
                params["uType"] = 3
            }
            disciplineRepository.getInfractionInstance(params).collect { result ->
                result.onSuccess { instance ->
                    updateState { it.copy(instanceCount = instance) }
                }.onFailure { /* ignore */ }
            }
        }
    }

    private fun onConsequenceSelected(consequence: String) {
        val state = (_uiState.value as? UiState.Success)?.data ?: return
        val selected = state.consequences.find { it.name == consequence }
        updateState {
            it.copy(isConsequenceSheetVisible = false, selectedConsequence = selected)
        }
    }

    private fun saveInfraction(action: Int) {
        viewModelScope.launch {
            val state = (_uiState.value as? UiState.Success)?.data ?: return@launch

            if (state.selectedSubcategory == null) {
                sendMessage("Please select a subcategory", MessageType.WARNING)
                return@launch
            }
            if (state.selectedConsequence == null) {
                sendMessage("Please select a consequence", MessageType.WARNING)
                return@launch
            }

            updateState { it.copy(isLoading = true) }

            val fileResult = withContext(Dispatchers.IO) {
                state.selectedFile?.file?.let { file ->
                    val base64 = Base64Utils.getBase64StringFromFile(file)
                    val ext = Base64Utils.getFileExtension(file)
                    base64 to ext
                }
            }

            val formatter = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault())
            val dateTime = formatter.format(Date())

            disciplineRepository.saveDisciplineLog(
                userId = userId,
                userType = userType.uTypeValue,
                action = action,
                infrSubTypeID = state.selectedSubcategory.id,
                consID = state.selectedConsequence.id,
                instance = state.instanceCount,
                correctiveAction = state.remarks.takeIf { it.isNotBlank() },
                infractionOn = dateTime,
                isComplianceActive = state.isComplianceActive,
                fileBase64 = fileResult?.first,
                fileExt = fileResult?.second,
            ).collect { result ->
                updateState { it.copy(isLoading = false) }
                result.onSuccess { message ->
                    sendMessage(message, MessageType.SUCCESS)
                    sendEvent(AddInfractionEvent.NavigateBack)
                }.onFailure { error ->
                    sendMessage(error.errorMessage(), MessageType.ERROR)
                }
            }
        }
    }

    private fun updateState(update: (AddInfractionUiState) -> AddInfractionUiState) {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) UiState.Success(update(currentState.data))
            else currentState
        }
    }

    private fun sendMessage(text: String, type: MessageType) {
        sendEvent(AddInfractionEvent.ShowMessage(SnackbarMessage(text, type)))
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DisciplineNavigationGraph.AddInfraction, AddInfractionViewModel> {
        override fun create(param: DisciplineNavigationGraph.AddInfraction): AddInfractionViewModel
    }
}

@Immutable
data class AddInfractionUiState(
    val isLoading: Boolean = false,
    val userInfo: DisciplineUserInfo? = null,
    val categories: List<InfractionType> = emptyList(),
    val subcategories: List<InfractionType> = emptyList(),
    val consequences: List<InfractionConsequence> = emptyList(),
    val selectedCategory: InfractionType? = null,
    val selectedSubcategory: InfractionType? = null,
    val selectedConsequence: InfractionConsequence? = null,
    val instanceCount: String = "0",
    val remarks: String = "",
    val isComplianceActive: Boolean = false,
    val selectedFile: SelectedFileDetails? = null,
    val isFileUploadSheetVisible: Boolean = false,
    val isCategorySheetVisible: Boolean = false,
    val isSubcategorySheetVisible: Boolean = false,
    val isConsequenceSheetVisible: Boolean = false,
)

sealed interface AddInfractionIntent {
    data object OnBackClicked : AddInfractionIntent
    data class OnCategorySelected(val category: String) : AddInfractionIntent
    data class OnSubcategorySelected(val subcategory: String) : AddInfractionIntent
    data class OnConsequenceSelected(val consequence: String) : AddInfractionIntent
    data class OnRemarksChanged(val remarks: String) : AddInfractionIntent
    data class OnComplianceToggled(val active: Boolean) : AddInfractionIntent
    data class OnFileSelected(val file: SelectedFileDetails) : AddInfractionIntent
    data object OnDeleteFile : AddInfractionIntent
    data object OnAddFileClicked : AddInfractionIntent
    data object OnDismissFileUploadSheet : AddInfractionIntent
    data object OnCategorySelectClicked : AddInfractionIntent
    data object OnSubcategorySelectClicked : AddInfractionIntent
    data object OnConsequenceSelectClicked : AddInfractionIntent
    data object OnDismissCategorySheet : AddInfractionIntent
    data object OnDismissSubcategorySheet : AddInfractionIntent
    data object OnDismissConsequenceSheet : AddInfractionIntent
    data class OnSaveClicked(val action: Int) : AddInfractionIntent
    data class OnShowError(val error: String) : AddInfractionIntent
}

sealed interface AddInfractionEvent {
    data object NavigateBack : AddInfractionEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : AddInfractionEvent
}
