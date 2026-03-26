package com.app.ecarepro.feature.survey.survey_questions

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.survey.SurveyAnswerRequest
import com.app.ecarepro.core.domain.model.survey.SurveyOption
import com.app.ecarepro.core.domain.model.survey.SurveyOptionAnswerPayload
import com.app.ecarepro.core.domain.model.survey.SurveyQuestion
import com.app.ecarepro.core.domain.model.survey.SurveyQuestionAnswerPayload
import com.app.ecarepro.core.domain.repository.SurveyRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.survey.R
import com.app.ecarepro.feature.survey.navigation.SurveyNavGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = SurveyQuestionsViewModel.Factory::class)
class SurveyQuestionsViewModel @AssistedInject constructor(
    @Assisted val navKey: SurveyNavGraph.SurveyQuestions,
    private val repository: SurveyRepository,
    @ApplicationContext private val context: Context,
) : BaseViewModel<SurveyQuestionsIntent, SurveyQuestionsEvent>() {

    private val _uiState = MutableStateFlow(SurveyQuestionsUiState())
    val uiState = _uiState.asStateFlow()

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<SurveyNavGraph.SurveyQuestions, SurveyQuestionsViewModel> {
        override fun create(param: SurveyNavGraph.SurveyQuestions): SurveyQuestionsViewModel
    }

    init {
        loadQuestions()
    }

    override fun handleIntent(intent: SurveyQuestionsIntent) {
        when (intent) {
            is SurveyQuestionsIntent.OnBackClicked -> sendEvent(SurveyQuestionsEvent.NavigateBack)
            is SurveyQuestionsIntent.OnSubmitClicked -> submitSurvey()
            is SurveyQuestionsIntent.OnSingleSelectChanged -> {
                _uiState.update {
                    it.copy(singleSelections = it.singleSelections + (intent.queID to intent.optID))
                }
            }
            is SurveyQuestionsIntent.OnMultiSelectToggled -> {
                _uiState.update { state ->
                    val current = state.multiSelections[intent.queID].orEmpty()
                    val updated = if (intent.optID in current) current - intent.optID else current + intent.optID
                    state.copy(multiSelections = state.multiSelections + (intent.queID to updated))
                }
            }
            is SurveyQuestionsIntent.OnTextResponseChanged -> {
                _uiState.update {
                    it.copy(textResponses = it.textResponses + (intent.queID to intent.text))
                }
            }
        }
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            repository.getSurveyQuestions(navKey.surveyId).collect { result ->
                result.fold(
                    onSuccess = { questions ->
                        _uiState.update {
                            it.copy(isLoading = false, questions = questions, isError = false)
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoading = false, isError = true, errorMessage = error.message)
                        }
                    },
                )
            }
        }
    }

    private fun submitSurvey() {
        val state = _uiState.value
        if (state.isSubmitting) return

        for (question in state.questions) {
            if (!question.isAnsMandatory) continue
            val isValid = when {
                question.textBoxOnly -> state.textResponses[question.queID]?.isNotBlank() == true
                question.isMultiSelect -> state.multiSelections[question.queID]?.isNotEmpty() == true
                else -> state.singleSelections[question.queID] != null
            }
            if (!isValid) {
                sendEvent(
                    SurveyQuestionsEvent.ShowMessage(
                        SnackbarMessage(context.getString(R.string.survey_answer_mandatory), MessageType.WARNING)
                    )
                )
                return
            }
        }

        val questionPayloads = state.questions.map { question ->
            if (question.textBoxOnly) {
                SurveyQuestionAnswerPayload(
                    queID = question.queID,
                    question = question.question,
                    isMultiSelect = question.isMultiSelect,
                    isAnsMandatory = question.isAnsMandatory,
                    response = question.response,
                    answer = state.textResponses[question.queID].orEmpty(),
                    options = emptyList(),
                )
            } else {
                val selectedOptions = question.options.filter { opt ->
                    isOptionSelected(state, question, opt.optID)
                }.map { opt ->
                    SurveyOptionAnswerPayload(
                        optID = opt.optID,
                        option = opt.option,
                        isSelected = true,
                        response = opt.response,
                    )
                }
                SurveyQuestionAnswerPayload(
                    queID = question.queID,
                    question = question.question,
                    isMultiSelect = question.isMultiSelect,
                    isAnsMandatory = question.isAnsMandatory,
                    response = question.response,
                    answer = "",
                    options = selectedOptions,
                )
            }
        }

        val request = SurveyAnswerRequest(
            id = navKey.surveyId,
            questions = questionPayloads,
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            repository.postSurveyAnswer(request).collect { result ->
                result.fold(
                    onSuccess = { message ->
                        sendEvent(
                            SurveyQuestionsEvent.ShowMessage(
                                SnackbarMessage(message, MessageType.SUCCESS)
                            )
                        )
                        sendEvent(SurveyQuestionsEvent.NavigateBack)
                    },
                    onFailure = { error ->
                        sendEvent(
                            SurveyQuestionsEvent.ShowMessage(
                                SnackbarMessage(error.message ?: "Failed to submit", MessageType.ERROR)
                            )
                        )
                    },
                )
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun isOptionSelected(
        state: SurveyQuestionsUiState,
        question: SurveyQuestion,
        optID: Int,
    ): Boolean = if (question.isMultiSelect) {
        optID in (state.multiSelections[question.queID].orEmpty())
    } else {
        state.singleSelections[question.queID] == optID
    }
}

@Immutable
data class SurveyQuestionsUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false,
    val questions: List<SurveyQuestion> = emptyList(),
    val singleSelections: Map<Int, Int> = emptyMap(),
    val multiSelections: Map<Int, Set<Int>> = emptyMap(),
    val textResponses: Map<Int, String> = emptyMap(),
)

sealed interface SurveyQuestionsIntent {
    data object OnBackClicked : SurveyQuestionsIntent
    data object OnSubmitClicked : SurveyQuestionsIntent
    data class OnSingleSelectChanged(val queID: Int, val optID: Int) : SurveyQuestionsIntent
    data class OnMultiSelectToggled(val queID: Int, val optID: Int) : SurveyQuestionsIntent
    data class OnTextResponseChanged(val queID: Int, val text: String) : SurveyQuestionsIntent
}

sealed interface SurveyQuestionsEvent {
    data object NavigateBack : SurveyQuestionsEvent
    data class ShowMessage(val message: SnackbarMessage) : SurveyQuestionsEvent
}
