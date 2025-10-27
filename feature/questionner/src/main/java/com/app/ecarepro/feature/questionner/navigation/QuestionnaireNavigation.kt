package com.app.ecarepro.feature.questionner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.questionner.QuestionnaireScreen
import com.app.ecarepro.feature.questionner.QuestionnaireViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface QuestionnaireNavigationGraph : NavKey {
    @Serializable
    data object Questionnaire : QuestionnaireNavigationGraph

    @Serializable
    data class QuestionDetail(val questionId: Int) : QuestionnaireNavigationGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryQuestionnaireNavigation(
    onBackClick: () -> Unit = {},
    navigateToQuestionDetail: (Int) -> Unit = {},
    navigateToCreateQuestion: () -> Unit = {},
) {
    entry<QuestionnaireNavigationGraph.Questionnaire> {
        QuestionnaireScreen(
            onBackClick = onBackClick,
            onQuestionClick = navigateToQuestionDetail,
        )
    }
}
