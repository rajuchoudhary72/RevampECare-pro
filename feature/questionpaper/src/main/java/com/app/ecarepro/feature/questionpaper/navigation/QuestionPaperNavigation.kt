package com.app.ecarepro.feature.questionpaper.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.questionpaper.QuestionPaperScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface QuestionPaperNavGraph : NavKey {
    @Serializable
    data object QuestionPaper : QuestionPaperNavGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.entryQuestionPaperNavigation(
    navigateBack: () -> Unit,
) {
    entry<QuestionPaperNavGraph.QuestionPaper> {
        QuestionPaperScreen(navigateBack = navigateBack)
    }
}
