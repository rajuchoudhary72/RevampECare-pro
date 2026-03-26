package com.app.ecarepro.feature.assignment.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Assignment
import com.app.ecarepro.core.domain.model.AssignmentStudent
import com.app.ecarepro.core.domain.repository.AcademicRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.assignment.AssignmentViewModel
import com.app.ecarepro.feature.assignment.navigation.AssignmentNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AssignmentDetailsViewModel.Factory::class)
class AssignmentDetailsViewModel @AssistedInject constructor(
    @Assisted val navKey: AssignmentNavigationGraph.AssignmentDetails,
    private val academicRepository: AcademicRepository,
) : BaseViewModel<AssignmentDetailsIntent, AssignmentDetailsEvent>() {

    private val assignment = navKey.assignment

    private val _uiState = MutableStateFlow(AssignmentDetailsUiState(assignment = assignment))
    val uiState = _uiState.asStateFlow()


    init {
        loadAssignmentDetails()
    }

    private fun loadAssignmentDetails() {
        viewModelScope.launch {
            combine(
                academicRepository.getAssignmentSubmissionReport(
                    id = assignment.id,
                    submitted = true
                ),
                academicRepository.getAssignmentSubmissionReport(
                    id = assignment.id,
                    submitted = false,
                )
            ) { submitted, notSubmitted ->
                submitted to notSubmitted
            }
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .collect { (submitted, notSubmitted) ->
                    if (submitted.isFailure || notSubmitted.isFailure) {
                        _uiState.update {
                            it.copy(
                                error = submitted.exceptionOrNull()?.errorMessage()
                                    ?: notSubmitted.exceptionOrNull()?.errorMessage(),
                                isLoading = false
                            )
                        }
                    } else {
                        val submittedList = submitted.getOrNull()?.studentList ?: emptyList()
                        val notSubmittedList = notSubmitted.getOrNull()?.studentList ?: emptyList()
                        val lateSubmittedList = notSubmitted.getOrNull()?.studentList?.filter { it.isLateSubmitted == true  } ?: emptyList()
                        _uiState.update {
                            it.copy(
                                submittedStudent = submittedList,
                                notSubmittedStudent = notSubmittedList,
                                lateSubmittedStudent = lateSubmittedList,
                                isLoading = false
                            )
                        }
                    }
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
        _uiState.update {
            it.copy(selectedTab = tab)
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<AssignmentNavigationGraph.AssignmentDetails, AssignmentDetailsViewModel> {
        override fun create(param: AssignmentNavigationGraph.AssignmentDetails): AssignmentDetailsViewModel
    }
}

@Immutable
data class AssignmentDetailsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val assignment: Assignment,
    val submittedStudent: List<AssignmentStudent> = emptyList(),
    val notSubmittedStudent: List<AssignmentStudent> = emptyList(),
    val lateSubmittedStudent: List<AssignmentStudent> = emptyList(),
    val selectedTab: SubmissionTab = SubmissionTab.SUBMITTED,
)


enum class SubmissionTab(val title: String) {
    SUBMITTED("Submitted"),
    NOT_SUBMITTED("Not Submitted"),

    LATE_SUBMITTED("Late Submitted");
}

sealed interface AssignmentDetailsIntent {
    data object OnBackClicked : AssignmentDetailsIntent
    data class OnTabSelected(val tab: SubmissionTab) : AssignmentDetailsIntent
}

sealed interface AssignmentDetailsEvent {
    data object NavigateBack : AssignmentDetailsEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : AssignmentDetailsEvent
}