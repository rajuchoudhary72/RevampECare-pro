package com.app.ecarepro.ui.splash

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    suspend fun getSliders() {
        schoolRepository.fetchWalkThroughData()
    }
}