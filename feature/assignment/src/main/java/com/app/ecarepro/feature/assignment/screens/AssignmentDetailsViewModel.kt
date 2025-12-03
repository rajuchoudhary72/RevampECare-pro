package com.app.ecarepro.feature.assignment.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AssignmentDetailsIntent, AssignmentDetailsEvent>() {

    private val _uiState = MutableStateFlow<UiState<AssignmentDetailsUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val assignmentId: String? = savedStateHandle["assignmentId"]

    init {
        loadAssignmentDetails()
    }

    private fun loadAssignmentDetails() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            val mockHeader = AssignmentDetailsHeader(
                title = "Physics Assignment",
                className = "9th class",
                subject = "English",
                date = "08 Aug 2025"
            )

            val mockStudents = List(20) { index ->
                StudentSubmissionItem(
                    id = index.toString(),
                    rollNo = index + 1,
                    name = if (index % 2 == 0) "Aditya Chauhan" else "Absam Khan",
                    submissionMode = "Offline",
                    isSubmitted = index < 15
                )
            }

            _uiState.update {
                UiState.Success(
                    AssignmentDetailsUiState(
                        headerDetails = mockHeader,
                        allStudents = mockStudents,
                        filteredStudents = mockStudents.filter { it.isSubmitted },
                        selectedTab = SubmissionTab.SUBMITTED
                    )
                )
            }
        }
    }

    override fun handleIntent(intent: AssignmentDetailsIntent) {
        when (intent) {
            is AssignmentDetailsIntent.OnBackClicked -> viewModelScope.launch {
                sendEvent(
                    AssignmentDetailsEvent.NavigateBack
                )
            }

            is AssignmentDetailsIntent.OnTabSelected -> changeTab(intent.tab)
        }
    }

    private fun changeTab(tab: SubmissionTab) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        val filtered = if (tab == SubmissionTab.SUBMITTED) {
            currentState.allStudents.filter { it.isSubmitted }
        } else {
            currentState.allStudents.filter { !it.isSubmitted }
        }

        _uiState.update {
            UiState.Success(currentState.copy(selectedTab = tab, filteredStudents = filtered))
        }
    }
}

@Immutable
data class AssignmentDetailsUiState(
    val headerDetails: AssignmentDetailsHeader,
    val allStudents: List<StudentSubmissionItem>,
    val filteredStudents: List<StudentSubmissionItem>,
    val selectedTab: SubmissionTab,
)

data class AssignmentDetailsHeader(
    val title: String,
    val className: String,
    val subject: String,
    val date: String,
)

data class StudentSubmissionItem(
    val id: String,
    val rollNo: Int,
    val name: String,
    val submissionMode: String,
    val isSubmitted: Boolean,
)

enum class SubmissionTab(val title: String) {
    SUBMITTED("Submitted"),
    NOT_SUBMITTED("Not Submitted")
}

sealed interface AssignmentDetailsIntent {
    data object OnBackClicked : AssignmentDetailsIntent
    data class OnTabSelected(val tab: SubmissionTab) : AssignmentDetailsIntent
}

sealed interface AssignmentDetailsEvent {
    data object NavigateBack : AssignmentDetailsEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : AssignmentDetailsEvent
}