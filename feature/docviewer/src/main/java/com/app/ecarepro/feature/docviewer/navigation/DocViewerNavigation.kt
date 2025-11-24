package com.app.ecarepro.feature.docviewer.navigation


import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.domain.model.DocType
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.docviewer.DocViewerScreen
import com.app.ecarepro.feature.docviewer.DocViewerViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface DocViewerNavigationGraph : NavKey {
    @Serializable
    data class DocViewer(
        val title: String,
        val docUrl: String,
        val docType: DocType? = null,
    ) : DocViewerNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntryDocViewerNavigation(
    navigateToBack: () -> Unit,
) {
    entry<DocViewerNavigationGraph.DocViewer> {
        val viewModel: DocViewerViewModel = navKeyViewModel(it)
        DocViewerScreen(
            viewModel = viewModel,
            onBackClick = navigateToBack
        )
    }
}
