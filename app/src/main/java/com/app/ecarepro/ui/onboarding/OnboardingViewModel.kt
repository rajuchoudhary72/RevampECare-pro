package com.app.ecarepro.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.Slide
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    val uiState =
        schoolRepository
            .getOnboardingSlides()
            .map { OnboardingUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                initialValue = OnboardingUiState.Loading,
                started = SharingStarted.WhileSubscribed(500)
            )

}


sealed interface OnboardingUiState {
    object Loading : OnboardingUiState
    data class Success(val slide: List<Slide>) : OnboardingUiState
}