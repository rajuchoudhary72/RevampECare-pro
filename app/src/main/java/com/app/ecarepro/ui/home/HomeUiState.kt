package com.app.ecarepro.ui.home

import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.Slider

sealed interface HomeUiState {
    object Loading : HomeUiState

    data class Error(val error: Throwable) : HomeUiState

    data class Success(
        val favourites: List<Slider>,
        val user: NetworkUserDetailsDto,
    ) : HomeUiState
}