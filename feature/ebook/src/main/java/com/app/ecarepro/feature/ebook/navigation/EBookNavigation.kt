package com.app.ecarepro.feature.ebook.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.ebook.ebook_main.EBookScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface EBookNavGraph : NavKey {
    @Serializable
    data object EBookMain : EBookNavGraph
}

fun EntryProviderBuilder<NavKey>.entryEBookNavigation(
    navigateBack: () -> Unit,
    navigateToPdf: (title: String, url: String) -> Unit,
) {
    entry<EBookNavGraph.EBookMain> {
        EBookScreen(
            navigateBack = navigateBack,
            navigateToPdf = navigateToPdf,
        )
    }
}
