package com.app.ecarepro.feature.assignment

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    // Inject repositories here, e.g. private val assignmentRepository: AssignmentRepository
) : BaseViewModel<AssignmentIntent, AssignmentEvent>() {

    private val _uiState: MutableStateFlow<UiState<AssignmentUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchAssignments()
    }

    private fun fetchAssignments() {
        // SIMULATING DATA FETCH
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            // Mock Data matching the screenshot
            val mockAssignments = listOf(
                Assignment(
                    id = "1",
                    title = "Physics assignment",
                    className = "9th class",
                    subject = "English",
                    createdDate = "08 Aug 2025",
                    dueDate = "22 Oct",
                    submittedCount = 24,
                    totalCount = 30,
                    isOverdue = false,
                    filePath = "http://sample.pdf"
                ),
                Assignment(
                    id = "2",
                    title = "Physics assignment",
                    className = "9th class",
                    subject = "English",
                    createdDate = "08 Aug 2025",
                    dueDate = "22 Oct",
                    submittedCount = 24,
                    totalCount = 30,
                    isOverdue = false,
                    filePath = "http://sample.pdf"
                ),
                Assignment(
                    id = "3",
                    title = "Physics assignment",
                    className = "9th class",
                    subject = "English",
                    createdDate = "08 Aug 2025",
                    dueDate = "22 Oct",
                    submittedCount = 24,
                    totalCount = 30,
                    isOverdue = true,
                    filePath = "http://sample.pdf" // Simulating overdue (Red/Orange)
                ),
                Assignment(
                    id = "4",
                    title = "Physics assignment",
                    className = "9th class",
                    subject = "English",
                    createdDate = "08 Aug 2025",
                    dueDate = "22 Oct",
                    submittedCount = 24,
                    totalCount = 30,
                    isOverdue = true,
                    filePath = "http://sample.pdf"
                )
            )

            _uiState.update {
                UiState.Success(
                    AssignmentUiState(
                        assignments = mockAssignments,
                        filteredAssignments = mockAssignments
                    )
                )
            }
        }
    }

    override fun handleIntent(intent: AssignmentIntent) {
        when (intent) {
            is AssignmentIntent.OnBackClicked -> viewModelScope.launch { sendEvent(AssignmentEvent.NavigateBack) }
            is AssignmentIntent.OnAddNewClicked -> viewModelScope.launch { sendEvent(AssignmentEvent.NavigateToAddAssignment) }
            is AssignmentIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is AssignmentIntent.OnViewClicked -> viewAssignment(intent.assignment)
            is AssignmentIntent.OnDownloadClicked -> downloadAssignment(intent.assignment)
            is AssignmentIntent.OnViewReportClicked -> sendEvent(AssignmentEvent.ViewReport)
        }
    }

    private fun onSearchQueryChanged(query: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = if (query.isBlank()) {
            currentState.assignments
        } else {
            currentState.assignments.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.className.contains(query, ignoreCase = true) ||
                        it.subject.contains(query, ignoreCase = true)
            }
        }

        _uiState.update {
            UiState.Success(currentState.copy(searchQuery = query, filteredAssignments = filtered))
        }
    }

    private fun viewAssignment(assignment: Assignment) {
        assignment.filePath?.let {
            viewModelScope.launch {
                sendEvent(AssignmentEvent.ViewAssignment(assignment.title, it))
            }
        }
    }

    private fun downloadAssignment(assignment: Assignment) {
        viewModelScope.launch {
            sendEvent(
                AssignmentEvent.ShowMessage(
                    SnackbarMessage(
                        "Download Started...",
                        MessageType.INFO
                    )
                )
            )
            // Add actual download logic here
        }
    }
}


// Domain Model (Sample)
data class Assignment(
    val id: String,
    val title: String,
    val className: String, val subject: String,
    val createdDate: String, // e.g., "08 Aug 2025"
    val dueDate: String,     // e.g., "22 Oct"
    val submittedCount: Int,
    val totalCount: Int,
    val isOverdue: Boolean,
    val filePath: String? = null,
)

// UI State
@Immutable
data class AssignmentUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val assignments: List<Assignment> = emptyList(),
    val filteredAssignments: List<Assignment> = emptyList(),
    // Bottom sheet states if needed like in Syllabus
    val isMenuVisible: Boolean = false,
    val selectedAssignmentId: String? = null,
)

// MVI Intent
sealed interface AssignmentIntent {
    data object OnBackClicked : AssignmentIntent
    data object OnAddNewClicked : AssignmentIntent
    data class OnSearchQueryChanged(val query: String) : AssignmentIntent
    data class OnViewClicked(val assignment: Assignment) : AssignmentIntent
    data class OnDownloadClicked(val assignment: Assignment) : AssignmentIntent
    data class OnViewReportClicked(val assignment: Assignment) : AssignmentIntent
}

// MVI Event (One-time effects)
sealed interface AssignmentEvent {
    data object NavigateBack : AssignmentEvent
    data object NavigateToAddAssignment : AssignmentEvent

    data object ViewReport : AssignmentEvent
    data class ViewAssignment(val title: String, val url: String) : AssignmentEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) :
        AssignmentEvent
}

