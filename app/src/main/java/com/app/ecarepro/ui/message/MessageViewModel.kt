package com.app.ecarepro.ui.message

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import kotlinx.coroutines.delay


@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    val userDataStore: UserDataStore,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    private val _showChatOption = MutableStateFlow(false)
    val showChatOption = _showChatOption

    private val _showDateRangePicker = MutableSharedFlow<Boolean>()
    val showDateRangePicker = _showDateRangePicker

    private val _clearFilter = MutableSharedFlow<Boolean>()
    val clearFilter = _clearFilter

    val messageSettings = MutableStateFlow<MessageSettings?>(null)

    val isFilterApplied = MutableStateFlow(false)
    val inboxMessageUnreadCount = MutableStateFlow<Pair<String, Int>?>(null)

    fun updateUnreadMessageCount(id: String) {
        Log.e("TAG", "updateUnreadMessageCount ID: ${id}", )
        inboxMessageUnreadCount.update { current ->
            if (current == null)
                Pair(id, 1)
            else
                current.copy(second = current.second + 1)
        }

        viewModelScope.launch {
            delay(1000)

            Log.e("TAG", "updateUnreadMessageCount: ${inboxMessageUnreadCount.value}")
        }
    }

    fun fetchMessageSettings() {
        viewModelScope.launch {
            messageRepository
                .getMessageSettings()
                .collectLatest { result ->
                    result.onSuccess { settings ->
                        userDataStore.saveMessageSettings(settings)
                        messageSettings.update { settings }
                    }
                }
        }
    }


    fun showDateRangePicker() {
        viewModelScope.launch {
            _showDateRangePicker.emit(true)
        }
    }

    fun toggleChatOption() {
        viewModelScope.launch {
            _showChatOption.update { it.not() }
        }
    }

    fun clearFilter() {
        viewModelScope.launch {
            _clearFilter.emit(true)
        }
    }

    fun sendScreenEvent() {
        analyticsManager.trackScreen(AnalyticsConstants.Screens.MESSAGE_LIST)
    }

    fun sendAnalyticEvent(
        event: String,
        attributes: Map<String, String>
    ) {
        analyticsManager.trackEvent(
            event,
            attributes
        )
    }

}