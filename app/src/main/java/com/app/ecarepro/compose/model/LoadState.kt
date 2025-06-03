package com.app.ecarepro.compose.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import android.util.Log


sealed interface LoadState {
    data object Loading : LoadState
    data object Nothing : LoadState

    sealed interface Error : LoadState {
        data class Exception(val error: Throwable) : Error
        data class ResourceId(val resourceId: Int) : Error
    }

    data class Message(val message: String) : LoadState

    fun isLoading() = this == Loading
    fun getErrorOrNull() = this as? Error
}

@Composable
fun LoadState.messageOrNull(): String? {
    return when (this) {
        is LoadState.Error.Exception -> this.error.message
        is LoadState.Error.ResourceId -> stringResource(this.resourceId)
        is LoadState.Message -> this.message
        else -> null
    }
}

