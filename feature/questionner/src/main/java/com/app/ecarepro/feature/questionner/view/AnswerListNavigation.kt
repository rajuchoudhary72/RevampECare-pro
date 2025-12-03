package com.app.ecarepro.feature.questionner.view

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class AnswerListRoute(
    val qid: Int
) : NavKey

@Composable
fun EntryProviderBuilder<NavKey>.EntryAnswerListNavigation(
    onBackClick: () -> Unit = {}
) {
    entry<AnswerListRoute> {
        AnswerListScreen(
            onNavigateBack = onBackClick
        )
    }
}
