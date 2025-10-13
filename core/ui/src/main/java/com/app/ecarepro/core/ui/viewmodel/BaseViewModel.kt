package com.app.ecarepro.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * A base class for ViewModels that follow the Model-View-Intent (MVI) pattern.
 *
 * @param Intent The type of the intents that the ViewModel can handle.
 * @param Event The type of the events that the ViewModel can emit.
 */
abstract class BaseViewModel<Intent, Event> : ViewModel() {

    // Channel for handling one-time screen events
    private val _screenEvent = Channel<Event>()
    val screenEvent = _screenEvent.receiveAsFlow()

    /**
     * An abstract function that must be implemented by subclasses to handle incoming intents.
     *
     * @param intent The intent to be processed.
     */
    abstract fun handleIntent(intent: Intent)

    /**
     * A utility function to send a screen event to the UI.
     * This is useful for one-time actions like navigation, showing a toast, etc.
     *
     * @param event The event to be sent.
     */
    protected fun sendEvent(event: Event) {
        viewModelScope.launch {
            _screenEvent.send(event)
        }
    }
}