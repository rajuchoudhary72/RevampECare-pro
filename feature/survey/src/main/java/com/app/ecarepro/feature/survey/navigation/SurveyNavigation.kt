package com.app.ecarepro.feature.survey.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.survey.survey_list.SurveyListScreen
import com.app.ecarepro.feature.survey.survey_questions.SurveyQuestionsScreen
import com.app.ecarepro.feature.survey.survey_questions.SurveyQuestionsViewModel
import com.app.ecarepro.feature.survey.survey_result.SurveyResultScreen
import com.app.ecarepro.feature.survey.survey_result.SurveyResultViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface SurveyNavGraph : NavKey {
    @Serializable
    data object SurveyList : SurveyNavGraph

    @Serializable
    data class SurveyQuestions(val surveyId: String) : SurveyNavGraph

    @Serializable
    data class SurveyResult(val surveyId: String) : SurveyNavGraph
}

fun EntryProviderBuilder<NavKey>.entrySurveyNavigation(
    navigateBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
) {
    entry<SurveyNavGraph.SurveyList> {
        SurveyListScreen(
            navigateBack = navigateBack,
            navigateToQuestions = { surveyId ->
                navigateTo(SurveyNavGraph.SurveyQuestions(surveyId))
            },
            navigateToResult = { surveyId ->
                navigateTo(SurveyNavGraph.SurveyResult(surveyId))
            },
        )
    }

    entry<SurveyNavGraph.SurveyQuestions> { key ->
        val viewModel: SurveyQuestionsViewModel = navKeyViewModel(key)
        SurveyQuestionsScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }

    entry<SurveyNavGraph.SurveyResult> { key ->
        val viewModel: SurveyResultViewModel = navKeyViewModel(key)
        SurveyResultScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
}
