package com.app.ecarepro.feature.message.common

import com.app.ecarepro.core.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal fun <T> MutableStateFlow<UiState<T>>.updateSuccess(transform: (T) -> T) {
    update { if (it is UiState.Success) UiState.Success(transform(it.data)) else it }
}
