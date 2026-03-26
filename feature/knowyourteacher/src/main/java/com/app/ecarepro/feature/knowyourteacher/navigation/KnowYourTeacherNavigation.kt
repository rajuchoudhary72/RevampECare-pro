package com.app.ecarepro.feature.knowyourteacher.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.knowyourteacher.KnowYourTeacherScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface KnowYourTeacherNavGraph : NavKey {
    @Serializable
    data object KnowYourTeacherList : KnowYourTeacherNavGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.entryKnowYourTeacherNavigation(
    navigateToBack: () -> Unit,
) {
    entry<KnowYourTeacherNavGraph.KnowYourTeacherList> {
        KnowYourTeacherScreen(navigateToBack = navigateToBack)
    }
}
