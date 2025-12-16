package com.app.ecarepro.feature.questionner

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.Question
import com.app.ecarepro.core.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionnaireViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestionnaireUiState(isLoading = true))
    val uiState: StateFlow<QuestionnaireUiState> = _uiState.asStateFlow()

    init {
        loadQuestions(reset = true)
    }

    fun handleIntent(intent: QuestionnaireIntent) {
        when (intent) {
            is QuestionnaireIntent.OnTabChanged -> {
                _uiState.update {
                    it.copy(
                        selectedTab = intent.tab,
                        questions = emptyList(),
                        currentPage = 1,
                        hasMorePages = true
                    )
                }
                loadQuestions(reset = true)
            }
            is QuestionnaireIntent.OnLikeClicked -> {
                toggleLike(intent.questionId)
            }
            QuestionnaireIntent.OnCreateNewClicked -> {
                // Handle navigation to create new question
            }
            QuestionnaireIntent.LoadMoreQuestions -> {
                loadMoreQuestions()
            }
            QuestionnaireIntent.OnRefresh -> {
                refresh()
            }
            is QuestionnaireIntent.OnQuestionClicked -> {
                // Handle navigation to question detail
            }
        }
    }

    private fun loadQuestions(reset: Boolean = false) {
        if (!reset && _uiState.value.isLoading) return

        viewModelScope.launch {
            val currentPage = if (reset) 1 else _uiState.value.currentPage

            _uiState.update { it.copy(isLoading = true) }

            userRepository.getQuestions(
                page = currentPage,
                myQuestions = _uiState.value.selectedTab == QuestionnaireTab.CREATED_BY_ME
            ).collect { result ->
                result
                    .onSuccess { response ->
                        val newQuestions = if (reset) {
                            response.questions
                        } else {
                            _uiState.value.questions + response.questions
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                questions = newQuestions,
                                totalQuestions = response.total,
                                hasMorePages = newQuestions.size < response.total,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "An error occurred"
                            )
                        }
                    }
            }
        }
    }

    private fun loadMoreQuestions() {
        if (_uiState.value.isLoading || !_uiState.value.hasMorePages) return

        _uiState.update {
            it.copy(currentPage = it.currentPage + 1)
        }
        loadQuestions()
    }

    private fun refresh() {
        _uiState.update {
            it.copy(
                currentPage = 1,
                questions = emptyList(),
                hasMorePages = true
            )
        }
        loadQuestions(reset = true)
    }

    private fun toggleLike(questionId: Int) {
        _uiState.update { state ->
            val updatedQuestions = state.questions.map { question ->
                if (question.qid == questionId) {
                    question.copy(
                        isILike = !question.isILike,
                        likes = if (question.isILike) question.likes - 1 else question.likes + 1
                    )
                } else {
                    question
                }
            }
            state.copy(questions = updatedQuestions)
        }
    }
}

// Intents from the UI to the ViewModel
sealed interface QuestionnaireIntent {
    data class OnTabChanged(val tab: QuestionnaireTab) : QuestionnaireIntent
    data class OnLikeClicked(val questionId: Int) : QuestionnaireIntent
    data class OnQuestionClicked(val questionId: Int) : QuestionnaireIntent
    data object OnCreateNewClicked : QuestionnaireIntent
    data object LoadMoreQuestions : QuestionnaireIntent
    data object OnRefresh : QuestionnaireIntent
}

enum class QuestionnaireTab {
    ALL,
    CREATED_BY_ME
}

@Immutable
data class QuestionnaireUiState(
    val selectedTab: QuestionnaireTab = QuestionnaireTab.ALL,
    val questions: List<Question> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val totalQuestions: Int = 0,
    val hasMorePages: Boolean = true
)
