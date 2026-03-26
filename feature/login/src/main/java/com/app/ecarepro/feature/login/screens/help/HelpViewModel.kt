package com.app.ecarepro.feature.login.screens.help

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = HelpViewModel.Factory::class)
class HelpViewModel @AssistedInject constructor(
    @Assisted val navKey: LoginNavigationGraph.Help,
    private val schoolRepository: SchoolRepository,
) : BaseViewModel<HelpIntent, HelpEvent>() {

    private val _uiState = MutableStateFlow(HelpUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSchoolDetails()
    }

    override fun handleIntent(intent: HelpIntent) {
        when (intent) {
            HelpIntent.OnBackClicked -> sendEvent(HelpEvent.NavigateBack)
            is HelpIntent.OnContactNumberClicked -> sendEvent(HelpEvent.OpenPhoneDialer(intent.phoneNumber))
            is HelpIntent.OnEmailClicked -> sendEvent(HelpEvent.OpenMailApp(intent.email))
        }
    }

    private fun loadSchoolDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val schoolDetails = schoolRepository.getSchoolDetail(navKey.schoolCode).first()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    schoolDetails = schoolDetails
                )
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<LoginNavigationGraph.Help, HelpViewModel> {
        override fun create(param: LoginNavigationGraph.Help): HelpViewModel
    }
}


sealed interface HelpIntent {
    data object OnBackClicked : HelpIntent
    data class OnContactNumberClicked(val phoneNumber: String) : HelpIntent
    data class OnEmailClicked(val email: String) : HelpIntent
}

sealed interface HelpEvent {
    data object NavigateBack : HelpEvent
    data class OpenPhoneDialer(val phoneNumber: String) : HelpEvent
    data class OpenMailApp(val email: String) : HelpEvent
}

@Immutable
data class HelpUiState(
    val isLoading: Boolean = false,
    val schoolDetails: SchoolDetail? = null,
)