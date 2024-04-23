package com.app.ecarepro.ui.home

import com.app.ecarepro.data.network.model.Card
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.Slider
import com.app.ecarepro.ui.message.inbox.InboxMessageUiState.Error

sealed interface HomeUiState {
    object Loading : HomeUiState

    data class Error(val error: Throwable) : HomeUiState

    data class Success(
        val favourites: List<Slider>,
        val cards: List<Card>,
        val user: NetworkUserDetailsDto,
    ) : HomeUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}