package com.app.ecarepro.feature.questionner.view.create_question

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object AddQuestionRoute : NavKey

@Composable
fun EntryProviderBuilder<NavKey>.EntryAddQuestionNavigation(
    onBackClick: () -> Unit = {}
) {
    entry<AddQuestionRoute> {
        AddQuestionScreen(
            onNavigateBack = onBackClick
        )
    }
}
