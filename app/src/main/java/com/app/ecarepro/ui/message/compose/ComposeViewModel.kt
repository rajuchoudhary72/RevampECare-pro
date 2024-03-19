package com.app.ecarepro.ui.message.compose

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

}