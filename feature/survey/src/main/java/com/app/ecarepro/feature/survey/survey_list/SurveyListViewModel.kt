package com.app.ecarepro.feature.survey.survey_list

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.survey.SurveyItem
import com.app.ecarepro.core.domain.repository.SurveyRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.survey.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyListViewModel @Inject constructor(
    private val repository: SurveyRepository,
    @ApplicationContext private val context: Context,
) : BaseViewModel<SurveyListIntent, SurveyListEvent>() {

    private val _uiState = MutableStateFlow<SurveyListUiState>(SurveyListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadSurveys()
    }

    override fun handleIntent(intent: SurveyListIntent) {
        when (intent) {
            is SurveyListIntent.OnBackClicked -> sendEvent(SurveyListEvent.NavigateBack)
            is SurveyListIntent.OnSurveyTapped -> handleSurveyTap(intent.card)
            is SurveyListIntent.Retry -> loadSurveys()
            is SurveyListIntent.Refresh -> loadSurveys()
        }
    }

    private fun loadSurveys() {
        viewModelScope.launch {
            _uiState.value = SurveyListUiState.Loading
            repository.getSurveyList().collect { result ->
                result.fold(
                    onSuccess = { items ->
                        _uiState.value = SurveyListUiState.Success(
                            items.map { SurveyCardPresentation.from(it) }
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = SurveyListUiState.Error(
                            error.message ?: "Failed to load surveys"
                        )
                    },
                )
            }
        }
    }

    private fun handleSurveyTap(card: SurveyCardPresentation) {
        when (val action = card.tapAction) {
            is SurveyTapAction.OpenQuestions -> {
                sendEvent(SurveyListEvent.NavigateToQuestions(action.surveyId))
            }
            is SurveyTapAction.ViewResult -> {
                sendEvent(SurveyListEvent.NavigateToResult(action.surveyId))
            }
            is SurveyTapAction.AlreadyResponded -> {
                sendEvent(
                    SurveyListEvent.ShowMessage(
                        SnackbarMessage(
                            context.getString(R.string.survey_already_responded),
                            MessageType.INFO,
                        )
                    )
                )
            }
            is SurveyTapAction.SurveyClosed -> {
                sendEvent(
                    SurveyListEvent.ShowMessage(
                        SnackbarMessage(context.getString(R.string.survey_closed), MessageType.INFO)
                    )
                )
            }
        }
    }
}

sealed interface SurveyListUiState {
    data object Loading : SurveyListUiState
    data class Success(val surveys: List<SurveyCardPresentation>) : SurveyListUiState
    data class Error(val message: String) : SurveyListUiState
}

@Immutable
data class SurveyCardPresentation(
    val id: String,
    val title: String,
    val description: String,
    val publishedOn: String,
    val openTill: String,
    val isOpen: Boolean,
    val isResponded: Boolean,
    val respondedOn: String?,
    val resultDeclared: Boolean,
) {
    val tapAction: SurveyTapAction
        get() = when {
            isOpen -> SurveyTapAction.OpenQuestions(id)
            resultDeclared -> SurveyTapAction.ViewResult(id)
            isResponded -> SurveyTapAction.AlreadyResponded
            else -> SurveyTapAction.SurveyClosed
        }

    companion object {
        fun from(item: SurveyItem) = SurveyCardPresentation(
            id = item.id,
            title = item.title,
            description = item.description,
            publishedOn = item.publishedOn,
            openTill = item.openEndDate,
            isOpen = item.isOpen,
            isResponded = item.isResponded,
            respondedOn = item.respondedOn,
            resultDeclared = item.resultDeclared,
        )
    }
}

sealed interface SurveyTapAction {
    data class OpenQuestions(val surveyId: String) : SurveyTapAction
    data class ViewResult(val surveyId: String) : SurveyTapAction
    data object AlreadyResponded : SurveyTapAction
    data object SurveyClosed : SurveyTapAction
}

sealed interface SurveyListIntent {
    data object OnBackClicked : SurveyListIntent
    data class OnSurveyTapped(val card: SurveyCardPresentation) : SurveyListIntent
    data object Retry : SurveyListIntent
    data object Refresh : SurveyListIntent
}

sealed interface SurveyListEvent {
    data object NavigateBack : SurveyListEvent
    data class NavigateToQuestions(val surveyId: String) : SurveyListEvent
    data class NavigateToResult(val surveyId: String) : SurveyListEvent
    data class ShowMessage(val message: SnackbarMessage) : SurveyListEvent
}
