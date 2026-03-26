package com.app.ecarepro.feature.library.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.library.book_detail.BookDetailScreen
import com.app.ecarepro.feature.library.book_detail.BookDetailViewModel
import com.app.ecarepro.feature.library.library_main.LibraryScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface LibraryNavGraph : NavKey {
    @Serializable
    data object LibraryMain : LibraryNavGraph

    @Serializable
    data class BookDetail(val bookId: Int) : LibraryNavGraph
}

fun EntryProviderBuilder<NavKey>.entryLibraryNavigation(
    navigateBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
) {
    entry<LibraryNavGraph.LibraryMain> {
        LibraryScreen(
            navigateBack = navigateBack,
            navigateToBookDetail = { bookId ->
                navigateTo(LibraryNavGraph.BookDetail(bookId))
            },
        )
    }
    entry<LibraryNavGraph.BookDetail> { key ->
        val viewModel: BookDetailViewModel = navKeyViewModel(key)
        BookDetailScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
}
