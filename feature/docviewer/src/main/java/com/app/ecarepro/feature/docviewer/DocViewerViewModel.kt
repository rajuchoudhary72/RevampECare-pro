package com.app.ecarepro.feature.docviewer

import androidx.compose.runtime.Immutable
import com.app.ecarepro.core.domain.model.DocType
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.feature.docviewer.navigation.DocViewerNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = DocViewerViewModel.Factory::class)
class DocViewerViewModel @AssistedInject constructor(
    @Assisted val navKey: DocViewerNavigationGraph.DocViewer,
) : BaseViewModel<DocViewerIntent, DocViewerEvent>() {

    private val _uiState: MutableStateFlow<DocViewerUiState> =
        MutableStateFlow(
            DocViewerUiState(
                title = navKey.title,
                docUrl = navKey.docUrl,
                docType = navKey.docType ?: DocType.fromUrl(navKey.docUrl)?: DocType.DOC
            )
        )
    val uiState = _uiState.asStateFlow()

    override fun handleIntent(intent: DocViewerIntent) {
        when (intent) {
            DocViewerIntent.OnBackClicked -> sendEvent(DocViewerEvent.NavigateBack)
            DocViewerIntent.OnShareClicked -> {}
        }
    }

    @AssistedFactory
    interface Factory :
        AssistedViewModelFactory<DocViewerNavigationGraph.DocViewer, DocViewerViewModel> {
        override fun create(param: DocViewerNavigationGraph.DocViewer): DocViewerViewModel
    }
}

@Immutable
data class DocViewerUiState(
    val isLoading: Boolean = false,
    val title: String,
    val docUrl: String,
    val docType: DocType,
)

sealed interface DocViewerEvent {
    data object NavigateBack : DocViewerEvent
}

sealed interface DocViewerIntent {
    data object OnBackClicked : DocViewerIntent
    data object OnShareClicked : DocViewerIntent
}