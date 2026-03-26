package com.app.ecarepro.feature.classteacher.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.classteacher.ClassTeacherScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface ClassTeacherNavGraph : NavKey {
    @Serializable
    data object ClassTeacherList : ClassTeacherNavGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.entryClassTeacherNavigation(
    navigateToBack: () -> Unit,
) {
    entry<ClassTeacherNavGraph.ClassTeacherList> {
        ClassTeacherScreen(navigateToBack = navigateToBack)
    }
}
