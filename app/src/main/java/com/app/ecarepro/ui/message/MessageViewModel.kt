package com.app.ecarepro.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    private val _showDateRangePicker = MutableSharedFlow<Boolean>()
    val showDateRangePicker = _showDateRangePicker

    private val messageSettings = MutableSharedFlow<MessageSettings>()

    init {
        getMessageSettings()
    }

    private fun getMessageSettings() {
        viewModelScope.launch {
            messageRepository
                .getMessageSettings()
                .collectLatest { result ->
                    result.onSuccess {
                        messageSettings.emit(it)
                    }
                }
        }
    }


    fun showDateRangePicker() {
        viewModelScope.launch {
            _showDateRangePicker.emit(true)
        }
    }
}