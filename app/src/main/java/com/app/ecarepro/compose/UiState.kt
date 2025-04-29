package com.app.ecarepro.compose

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>

    data class Error(val error: Throwable) : UiState<Nothing>

    fun isLoading() = this == Loading

    fun getErrorOrNull() = (this as? Error)?.error

    fun getValueOrNull() = (this as? Success)?.data
}