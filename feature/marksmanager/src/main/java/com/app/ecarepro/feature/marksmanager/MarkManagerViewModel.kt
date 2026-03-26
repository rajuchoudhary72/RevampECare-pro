package com.app.ecarepro.feature.marksmanager

import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.repository.MarkManagerRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.feature.marksmanager.navigation.MarkManagerNavGraph
import com.app.ecarepro.feature.marksmanager.navigation.MarkManagerType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MarkManagerViewModel.Factory::class)
class MarkManagerViewModel @AssistedInject constructor(
    @Assisted val navKey: MarkManagerNavGraph.MarkManager,
    private val repository: MarkManagerRepository,
) : BaseViewModel<MarkManagerIntent, MarkManagerEvent>() {

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadUrl()
    }

    override fun handleIntent(intent: MarkManagerIntent) {
        when (intent) {
            MarkManagerIntent.OnBackClicked -> sendEvent(MarkManagerEvent.NavigateBack)
            MarkManagerIntent.OnRetry -> loadUrl()
        }
    }

    private fun loadUrl() {
        val flow = when (navKey.type) {
            MarkManagerType.MARKS_ENTRY -> repository.getMarkManagerUrl()
            MarkManagerType.SCHOOL_WEBSITE -> repository.getSchoolWebsiteUrl()
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            flow.collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { url -> UiState.Success(url) },
                    onFailure = { e ->
                        UiState.Error(e.message ?: "Failed to open page")
                    },
                )
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<MarkManagerNavGraph.MarkManager, MarkManagerViewModel> {
        override fun create(param: MarkManagerNavGraph.MarkManager): MarkManagerViewModel
    }
}

sealed interface MarkManagerIntent {
    data object OnBackClicked : MarkManagerIntent
    data object OnRetry : MarkManagerIntent
}

sealed interface MarkManagerEvent {
    data object NavigateBack : MarkManagerEvent
}
