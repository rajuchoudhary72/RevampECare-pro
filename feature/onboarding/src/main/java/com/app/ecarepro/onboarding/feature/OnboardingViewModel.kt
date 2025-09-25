package com.app.ecarepro.onboarding.feature

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.usecase.GetOnboardingItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getOnboardingItemUseCase: GetOnboardingItemUseCase
): ViewModel(){
    init {
        viewModelScope.launch {
            getOnboardingItemUseCase()
                .collect {
                    Log.e("HAri", it.toString(), )
                }
        }
    }
}