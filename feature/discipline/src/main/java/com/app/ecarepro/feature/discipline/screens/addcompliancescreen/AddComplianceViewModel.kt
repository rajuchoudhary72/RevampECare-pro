package com.app.ecarepro.feature.discipline.screens.addcompliancescreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.discipline.DisciplineUserInfo
import com.app.ecarepro.core.domain.model.discipline.InfractionRecord
import com.app.ecarepro.core.domain.repository.DisciplineRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.common.Base64Utils
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SelectedFileDetails
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
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

@HiltViewModel(assistedFactory = AddComplianceViewModel.Factory::class)
class AddComplianceViewModel @AssistedInject constructor(
    @Assisted val navKey: DisciplineNavigationGraph.AddCompliance,
    private val disciplineRepository: DisciplineRepository,
) : BaseViewModel<AddComplianceIntent, AddComplianceEvent>() {

    private val infractionID = navKey.infractionID
    private val userType = navKey.userType
    private val userId = navKey.userId

    private val _uiState: MutableStateFlow<UiState<AddComplianceUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchInfractionDetails()
    }

    override fun handleIntent(intent: AddComplianceIntent) {
        when (intent) {
            AddComplianceIntent.OnBackClicked -> sendEvent(AddComplianceEvent.NavigateBack)
            is AddComplianceIntent.OnRemarksChanged -> updateState { it.copy(complianceText = intent.text) }
            is AddComplianceIntent.OnFileSelected -> updateState { it.copy(selectedFile = intent.file, isFileUploadSheetVisible = false) }
            AddComplianceIntent.OnDeleteFile -> updateState { it.copy(selectedFile = null) }
            AddComplianceIntent.OnAddFileClicked -> updateState { it.copy(isFileUploadSheetVisible = true) }
            AddComplianceIntent.OnDismissFileUploadSheet -> updateState { it.copy(isFileUploadSheetVisible = false) }
            AddComplianceIntent.OnSaveCompliance -> saveCompliance()
            AddComplianceIntent.OnResolve -> resolveCompliance()
            is AddComplianceIntent.OnShowError -> sendMessage(intent.error, MessageType.ERROR)
        }
    }

    private fun fetchInfractionDetails() {
        viewModelScope.launch {
            val flow = if (userType == DisciplineUserType.STUDENT) {
                disciplineRepository.getStudentInfractions(userId)
            } else {
                disciplineRepository.getStaffInfractions(userId)
            }

            flow.collect { result ->
                result.onSuccess { details ->
                    val record = details.records.find { it.infractionID == infractionID }
                    _uiState.update {
                        UiState.Success(
                            AddComplianceUiState(
                                infraction = record,
                                userInfo = details.userInfo,
                            )
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { UiState.Error(error.errorMessage()) }
                }
            }
        }
    }

    private fun saveCompliance() {
        viewModelScope.launch {
            val state = (_uiState.value as? UiState.Success)?.data ?: return@launch
            updateState { it.copy(isLoading = true) }

            val fileResult = withContext(Dispatchers.IO) {
                state.selectedFile?.file?.let { file ->
                    val base64 = Base64Utils.getBase64StringFromFile(file)
                    val ext = Base64Utils.getFileExtension(file)
                    base64 to ext
                }
            }

            disciplineRepository.saveCompliance(
                uType = userType.complianceUType,
                id = infractionID,
                compliance = state.complianceText,
                fileBase64 = fileResult?.first,
                fileExt = fileResult?.second,
            ).collect { result ->
                updateState { it.copy(isLoading = false) }
                result.onSuccess { message ->
                    sendMessage(message, MessageType.SUCCESS)
                    sendEvent(AddComplianceEvent.NavigateBack)
                }.onFailure { error ->
                    sendMessage(error.errorMessage(), MessageType.ERROR)
                }
            }
        }
    }

    private fun resolveCompliance() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            disciplineRepository.resolveCompliance(userType.complianceUType, infractionID)
                .collect { result ->
                    updateState { it.copy(isLoading = false) }
                    result.onSuccess { message ->
                        sendMessage(message, MessageType.SUCCESS)
                        sendEvent(AddComplianceEvent.NavigateBack)
                    }.onFailure { error ->
                        sendMessage(error.errorMessage(), MessageType.ERROR)
                    }
                }
        }
    }

    private fun updateState(update: (AddComplianceUiState) -> AddComplianceUiState) {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) UiState.Success(update(currentState.data))
            else currentState
        }
    }

    private fun sendMessage(text: String, type: MessageType) {
        sendEvent(AddComplianceEvent.ShowMessage(SnackbarMessage(text, type)))
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DisciplineNavigationGraph.AddCompliance, AddComplianceViewModel> {
        override fun create(param: DisciplineNavigationGraph.AddCompliance): AddComplianceViewModel
    }
}

@Immutable
data class AddComplianceUiState(
    val isLoading: Boolean = false,
    val infraction: InfractionRecord? = null,
    val userInfo: DisciplineUserInfo? = null,
    val complianceText: String = "",
    val selectedFile: SelectedFileDetails? = null,
    val isFileUploadSheetVisible: Boolean = false,
)

sealed interface AddComplianceIntent {
    data object OnBackClicked : AddComplianceIntent
    data class OnRemarksChanged(val text: String) : AddComplianceIntent
    data class OnFileSelected(val file: SelectedFileDetails) : AddComplianceIntent
    data object OnDeleteFile : AddComplianceIntent
    data object OnAddFileClicked : AddComplianceIntent
    data object OnDismissFileUploadSheet : AddComplianceIntent
    data object OnSaveCompliance : AddComplianceIntent
    data object OnResolve : AddComplianceIntent
    data class OnShowError(val error: String) : AddComplianceIntent
}

sealed interface AddComplianceEvent {
    data object NavigateBack : AddComplianceEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : AddComplianceEvent
}
