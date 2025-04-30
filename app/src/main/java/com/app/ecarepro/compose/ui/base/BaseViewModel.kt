package com.app.ecarepro.compose.ui.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.compose.model.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A ViewModel responsible for managing the state of loading, errors, and messages.
 *
 * This ViewModel exposes a SharedFlow of LoadState objects, which the UI can collect to
 * react to changes in the state.
 */
@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    /**
     * The internal MutableSharedFlow that emits LoadState objects.
     */
    private val _loadState = MutableSharedFlow<LoadState>(extraBufferCapacity = 1)

    /**
     * The public SharedFlow that the UI observes to get the current LoadState.
     */
    val loadState: SharedFlow<LoadState> = _loadState.asSharedFlow()

    /**
     * Updates the load state to show or hide the loading indicator.
     *
     * @param show True to show the loading indicator, false to hide it.
     */
    fun showLoading(show: Boolean) {
        emitState(if (show) LoadState.Loading else LoadState.Nothing)
    }

    /**
     * Updates the load state to show an error represented by an exception.
     *
     * @param error The exception representing the error.
     */
    fun showError(error: Throwable) {
        emitState(LoadState.Error.Exception(error))
    }

    /**
     * Updates the load state to show an error message represented by a string resource ID.
     *
     * @param resourceId The string resource ID of the error message.
     */
    fun showErrorMessage(@StringRes resourceId: Int) {
        emitState(LoadState.Error.ResourceId(resourceId))
    }

    /**
     * Updates the load state to show a general message.
     *
     * @param message The message to be shown.
     */
    fun showMessage(message: String) {
        emitState(LoadState.Message(message))
    }

    /**
     * Emits a new LoadState to the SharedFlow.
     *
     * @param state The LoadState to emit.
     */
    private fun emitState(state: LoadState) {
        viewModelScope.launch {
            _loadState.emit(state)
        }
    }

    fun <T> Flow<Result<T>>.handleResultWithLoadState(): Flow<Result<T>> {
        return this
            .onStart { showLoading(true) }
            .onCompletion { showLoading(false) }
            .catch { showError(it) }

    }
}