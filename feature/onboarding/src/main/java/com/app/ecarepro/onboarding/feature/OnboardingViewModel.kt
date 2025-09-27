package com.app.ecarepro.onboarding.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.OnboardingItem
import com.app.ecarepro.core.domain.usecase.GetOnboardingItemUseCase
import com.app.ecarepro.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getOnboardingItemUseCase: GetOnboardingItemUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<OnboardingItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<OnboardingItem>>> = _uiState.asStateFlow()

    init {
        fetchOnboardingItems()
    }

    fun fetchOnboardingItems() {
        _uiState.value = UiState.Loading

        getOnboardingItemUseCase()
            .onEach { result ->
                result.fold(
                    onSuccess = { items ->
                        _uiState.value = UiState.Success(items)
                    },
                    onFailure = { throwable ->
                        _uiState.value = UiState.Error(
                            throwable.localizedMessage ?: "An unexpected error occurred"
                        )
                    }
                )
            }
            .catch { e ->
                _uiState.value = UiState.Error(
                    e.localizedMessage ?: "An error occurred in the data flow"
                )
            }
            .launchIn(viewModelScope)
    }

}

