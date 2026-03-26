package com.app.ecarepro.feature.assignment

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Assignment
import com.app.ecarepro.core.domain.repository.AcademicRepository
import com.app.ecarepro.core.download.FileDownloader
import com.app.ecarepro.core.download.model.DownloadRequest
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.assignment.AssignmentEvent.NavigateBack
import com.app.ecarepro.feature.assignment.AssignmentEvent.NavigateToAddAssignment
import com.app.ecarepro.feature.assignment.AssignmentEvent.ViewReport
import com.app.ecarepro.feature.assignment.screens.AddAssignmentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val academicRepository: AcademicRepository,
    private val fileDownloader: FileDownloader,
) : BaseViewModel<AssignmentIntent, AssignmentEvent>() {

    private val _uiState: MutableStateFlow<UiState<AssignmentUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchAssignments()
    }

    private fun fetchAssignments(refresh: Boolean = false) {
        viewModelScope.launch {
            academicRepository.getTeacherAssignments()
                .onStart {
                    _uiState.update { currentState ->
                        if (refresh) {
                            // If refreshing, keep the current data on screen but show the spinner
                            (currentState as? UiState.Success)?.let { successState ->
                                UiState.Success(successState.data.copy(isRefresing = true))
                            } ?: currentState
                        } else {
                            // If not refreshing (cold load), show full screen loader
                            UiState.Loading
                        }
                    }
                }
                .collect { result ->
                    result.onSuccess { assignments ->
                        _uiState.update {
                            UiState.Success(
                                AssignmentUiState(
                                    assignments = assignments,
                                    filteredAssignments = assignments, // Assuming no filter initially
                                    isRefresing = false
                                )
                            )
                        }
                    }.onFailure { error ->
                        handleFetchError(error, refresh)
                    }
                }
        }
    }

    // Extract error handling to reduce nesting and complexity
    private fun handleFetchError(error: Throwable, isRefresh: Boolean) {
        val message = error.errorMessage() // Your extension function

        if (isRefresh) {
            // If refreshing, revert the 'isRefresing' flag in UI state
            _uiState.update { currentState ->
                (currentState as? UiState.Success)?.let { successState ->
                    UiState.Success(successState.data.copy(isRefresing = false))
                } ?: currentState
            }

            // And show a transient message
            sendEvent(
                AssignmentEvent.ShowMessage(
                    SnackbarMessage(message, MessageType.ERROR)
                )
            )
        } else {
            // If cold load failed, show error screen
            _uiState.update { UiState.Error(message) }
        }
    }


    override fun handleIntent(intent: AssignmentIntent) {
        when (intent) {
            AssignmentIntent.OnRefresh -> fetchAssignments(true)
            is AssignmentIntent.OnBackClicked -> viewModelScope.launch { sendEvent(NavigateBack) }
            is AssignmentIntent.OnAddNewClicked -> viewModelScope.launch {
                sendEvent(
                    NavigateToAddAssignment
                )
            }

            is AssignmentIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is AssignmentIntent.OnViewClicked -> viewAssignment(intent.assignment)
            is AssignmentIntent.OnDownloadClicked -> downloadAssignment(intent.assignment)
            is AssignmentIntent.OnViewReportClicked -> sendEvent(ViewReport(intent.assignment))
            is AssignmentIntent.OnMenuClicked -> showMenuBottomSheet(intent.assignment)
            AssignmentIntent.OnDismissDeleteBottomSheet -> dismissDeleteBottomSheet()
            AssignmentIntent.OnDismissMenu -> dismissMenuBottomSheet()
            is AssignmentIntent.OnEditClicked -> editAssignment(intent.assignment)
            is AssignmentIntent.ShowDeleteBottomSheet -> showDeleteBottomSheet()
            is AssignmentIntent.OnDeleteClicked -> deleteAssignment(intent.assignment)

        }
    }

    private fun deleteAssignment(assignment: Assignment?) {
        if (assignment == null) return
        viewModelScope.launch {
            academicRepository
                .deleteAssignment(assignment.id)
                .onStart {
                    updateState {
                        it.copy(
                            isLoading = true,
                            isDeleteSheetVisible = false
                        )
                    }
                }
                .collect { result ->
                    updateState {
                        it.copy(isLoading = false)
                    }
                    result
                        .onSuccess { message ->
                            sendEvent(
                                AssignmentEvent.ShowMessage(
                                    SnackbarMessage(
                                        message,
                                        MessageType.SUCCESS
                                    )
                                )
                            )
                            handleIntent(AssignmentIntent.OnRefresh)
                        }
                        .onFailure { error ->
                            sendEvent(
                                AssignmentEvent.ShowMessage(
                                    SnackbarMessage(
                                        error.errorMessage(),
                                        MessageType.ERROR
                                    )
                                )
                            )
                        }
                }
        }
    }

    private fun updateState(update: (AssignmentUiState) -> AssignmentUiState) {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(update(currentState.data))
            } else {
                currentState
            }
        }
    }
    private fun showDeleteBottomSheet() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isDeleteSheetVisible = true,
                    isMenuVisible = false
                )
            )
        }
    }

    private fun editAssignment(assignment: Assignment?) {
        if (assignment != null) {
            sendEvent(AssignmentEvent.EditAssignment(assignment))
            dismissMenuBottomSheet()
        }
    }

    private fun showMenuBottomSheet(assignment: Assignment) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isMenuVisible = true,
                    selectedAssignment = assignment
                )
            )
        }
    }

    private fun dismissMenuBottomSheet() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isMenuVisible = false,
                    selectedAssignment = null
                )
            )
        }
    }

    private fun dismissDeleteBottomSheet() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isDeleteSheetVisible = false,
                    selectedAssignment = null
                )
            )
        }
    }

    private fun onSearchQueryChanged(query: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = if (query.isBlank()) {
            currentState.assignments
        } else {
            currentState.assignments.filter {
                it.title?.contains(query, ignoreCase = true) == true ||
                        it.classX?.contains(query, ignoreCase = true) == true ||
                        it.subject?.contains(query, ignoreCase = true) == true
            }
        }

        _uiState.update {
            UiState.Success(currentState.copy(searchQuery = query, filteredAssignments = filtered))
        }
    }

    private fun viewAssignment(assignment: Assignment) {
        viewModelScope.launch {
            // Collect all attachment URLs from both asgFile and asgFiles
            val attachmentUrls = mutableListOf<String>()

            // Check if multiple files exist first
            val multipleFiles = assignment.asgFiles?.filterNotNull()
            if (!multipleFiles.isNullOrEmpty()) {
                // Add multiple files only if they exist
                attachmentUrls.addAll(multipleFiles)
            } else {
                // Add single file only if multiple files don't exist
                assignment.asgFile?.let { attachmentUrls.add(it) }
            }

            if (attachmentUrls.isNotEmpty()) {
                sendEvent(
                    AssignmentEvent.ViewAttachments(
                        title = assignment.title ?: "Assignment Attachments",
                        attachmentUrls = attachmentUrls
                    )
                )
            } else {
                sendEvent(
                    AssignmentEvent.ShowMessage(
                        SnackbarMessage(
                            "No attachments available.",
                            MessageType.INFO
                        )
                    )
                )
            }
        }
    }

    private fun downloadAssignment(assignment: Assignment) {
        viewModelScope.launch {
            assignment.asgFile?.let { url ->
                viewModelScope.launch(Dispatchers.IO) {
                    val title = assignment.title ?: "Assignment"

                    try {
                        fileDownloader.download(
                            DownloadRequest(
                                url = url,
                                fileName = title
                            )
                        ).first()

                        withContext(Dispatchers.Main) {
                            sendEvent(
                                AssignmentEvent.ShowMessage(
                                    SnackbarMessage("Download Started", MessageType.INFO)
                                )
                            )
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            sendEvent(
                                AssignmentEvent.ShowMessage(
                                    SnackbarMessage(
                                        "Download failed. Please try again.",
                                        MessageType.ERROR
                                    )
                                )
                            )
                        }
                    }
                }
            } ?: run {
                sendEvent(
                    AssignmentEvent.ShowMessage(
                        SnackbarMessage(
                            "Assignment file path is not available.",
                            MessageType.ERROR
                        )
                    )
                )
            }
        }
    }
}

// UI State
@Immutable
data class AssignmentUiState(
    val isLoading: Boolean = false,

    val isRefresing: Boolean = false,
    val searchQuery: String = "",
    val assignments: List<Assignment> = emptyList(),
    val filteredAssignments: List<Assignment> = emptyList(),
    val selectedAssignmentId: String? = null,
    val isMenuVisible: Boolean = false,
    val isDeleteSheetVisible: Boolean = false,
    val selectedAssignment: Assignment? = null,
)

// MVI Intent
sealed interface AssignmentIntent {
    data object OnBackClicked : AssignmentIntent

    data object OnRefresh : AssignmentIntent

    data object OnAddNewClicked : AssignmentIntent
    data class OnSearchQueryChanged(val query: String) : AssignmentIntent
    data class OnViewClicked(val assignment: Assignment) : AssignmentIntent
    data class OnDownloadClicked(val assignment: Assignment) : AssignmentIntent
    data class OnViewReportClicked(val assignment: Assignment) : AssignmentIntent

    data class OnMenuClicked(val assignment: Assignment) : AssignmentIntent

    data object OnDismissMenu : AssignmentIntent

    data object OnDismissDeleteBottomSheet : AssignmentIntent

    data class OnEditClicked(val assignment: Assignment?) : AssignmentIntent

    data object ShowDeleteBottomSheet : AssignmentIntent
    data class OnDeleteClicked(val assignment: Assignment?) : AssignmentIntent
}

// MVI Event (One-time effects)
sealed interface AssignmentEvent {
    data object NavigateBack : AssignmentEvent
    data object NavigateToAddAssignment : AssignmentEvent

    data class ViewReport(val assignment: Assignment) : AssignmentEvent
    data class EditAssignment(val assignment: Assignment) : AssignmentEvent
    data class ViewAssignment(val title: String, val url: String) : AssignmentEvent
    data class ViewAttachments(val title: String, val attachmentUrls: List<String>) : AssignmentEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) :
        AssignmentEvent
}

