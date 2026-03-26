package com.app.ecarepro.feature.survey.survey_result

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.survey.SurveyQuestion
import com.app.ecarepro.core.domain.repository.SurveyRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.feature.survey.navigation.SurveyNavGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@HiltViewModel(assistedFactory = SurveyResultViewModel.Factory::class)
class SurveyResultViewModel @AssistedInject constructor(
    @Assisted val navKey: SurveyNavGraph.SurveyResult,
    private val repository: SurveyRepository,
) : BaseViewModel<SurveyResultIntent, SurveyResultEvent>() {

    private val _uiState = MutableStateFlow<SurveyResultUiState>(SurveyResultUiState.Loading)
    val uiState = _uiState.asStateFlow()

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<SurveyNavGraph.SurveyResult, SurveyResultViewModel> {
        override fun create(param: SurveyNavGraph.SurveyResult): SurveyResultViewModel
    }

    init {
        loadResult()
    }

    override fun handleIntent(intent: SurveyResultIntent) {
        when (intent) {
            is SurveyResultIntent.OnBackClicked -> sendEvent(SurveyResultEvent.NavigateBack)
            is SurveyResultIntent.Retry -> loadResult()
        }
    }

    private fun loadResult() {
        viewModelScope.launch {
            _uiState.value = SurveyResultUiState.Loading
            repository.getSurveyResult(navKey.surveyId).collect { result ->
                result.fold(
                    onSuccess = { questions ->
                        _uiState.value = SurveyResultUiState.Success(
                            questions.map { SurveyResultPresentation.from(it) }
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = SurveyResultUiState.Error(
                            error.message ?: "Failed to load results"
                        )
                    },
                )
            }
        }
    }
}

sealed interface SurveyResultUiState {
    data object Loading : SurveyResultUiState
    data class Success(val results: List<SurveyResultPresentation>) : SurveyResultUiState
    data class Error(val message: String) : SurveyResultUiState
}

@Immutable
data class SurveyResultPresentation(
    val queID: Int,
    val questionText: String,
    val totalResponses: Int,
    val options: List<SurveyResultOptionPresentation>,
) {
    companion object {
        fun from(question: SurveyQuestion): SurveyResultPresentation {
            val totalOptionResponses = question.options.sumOf { it.response }
            return SurveyResultPresentation(
                queID = question.queID,
                questionText = question.question,
                totalResponses = question.response,
                options = question.options.map { opt ->
                    val pct = if (totalOptionResponses > 0) {
                        ((opt.response.toDouble() / totalOptionResponses) * 100).roundToInt()
                    } else 0
                    SurveyResultOptionPresentation(
                        optionText = opt.option,
                        responseCount = opt.response,
                        percentage = pct,
                    )
                },
            )
        }
    }
}

@Immutable
data class SurveyResultOptionPresentation(
    val optionText: String,
    val responseCount: Int,
    val percentage: Int,
)

sealed interface SurveyResultIntent {
    data object OnBackClicked : SurveyResultIntent
    data object Retry : SurveyResultIntent
}

sealed interface SurveyResultEvent {
    data object NavigateBack : SurveyResultEvent
}
