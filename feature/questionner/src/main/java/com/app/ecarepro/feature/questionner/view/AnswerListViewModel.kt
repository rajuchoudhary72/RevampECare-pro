package com.app.ecarepro.feature.questionner.view

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.Answer
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
class AnswerListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnswerListUiState())
    val uiState: StateFlow<AnswerListUiState> = _uiState.asStateFlow()

    init {
        // Get question ID from navigation arguments
        val qid = savedStateHandle.get<String>("qid")?.toIntOrNull() ?: 0

        _uiState.update {
            it.copy(qid = qid)
        }

        if (qid > 0) {
            loadAnswers()
        }
    }

    fun handleIntent(intent: AnswerListIntent) {
        when (intent) {
            is AnswerListIntent.LoadAnswers -> loadAnswers()
            is AnswerListIntent.PostAnswer -> postAnswer(intent.answerText)
            is AnswerListIntent.DeleteAnswer -> deleteAnswer(intent.answerId)
            is AnswerListIntent.OnAnswerTextChanged -> {
                _uiState.update { it.copy(currentAnswerText = intent.text) }
            }
            AnswerListIntent.OnRefresh -> {
                _uiState.update { it.copy(answers = emptyList()) }
                loadAnswers()
            }
        }
    }

    private fun loadAnswers() {
        val qid = _uiState.value.qid
        if (qid <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.getAnswerList(qid).collect { result ->
                result
                    .onSuccess { response ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                answers = response.answers,
                                question = response.question,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to load answers"
                            )
                        }
                    }
            }
        }
    }

    private fun postAnswer(answerText: String) {
        if (answerText.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Answer cannot be empty") }
            return
        }

        val qid = _uiState.value.qid
        if (qid <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPostingAnswer = true, errorMessage = null) }

            userRepository.postAnswer(qid, answerText).collect { result ->
                result
                    .onSuccess { response ->
                        _uiState.update {
                            it.copy(
                                isPostingAnswer = false,
                                currentAnswerText = "",
                                successMessage = response.message
                            )
                        }
                        // Reload answers after successful post
                        loadAnswers()
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isPostingAnswer = false,
                                errorMessage = error.message ?: "Failed to post answer"
                            )
                        }
                    }
            }
        }
    }

    private fun deleteAnswer(answerId: Int) {
        // TODO: Implement delete answer API call when available
        // For now, we'll just remove it from the UI
        _uiState.update { state ->
            state.copy(
                answers = state.answers.filter { it.anID != answerId }
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}

// Intents from the UI to the ViewModel
sealed interface AnswerListIntent {
    data object LoadAnswers : AnswerListIntent
    data class PostAnswer(val answerText: String) : AnswerListIntent
    data class DeleteAnswer(val answerId: Int) : AnswerListIntent
    data class OnAnswerTextChanged(val text: String) : AnswerListIntent
    data object OnRefresh : AnswerListIntent
}

@Immutable
data class AnswerListUiState(
    val qid: Int = 0,
    val question: Question? = null,
    val answers: List<Answer> = emptyList(),
    val currentAnswerText: String = "",
    val isLoading: Boolean = false,
    val isPostingAnswer: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
