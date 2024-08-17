package com.app.ecarepro.ui.appointment.v2

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.staff.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {
    val loadState = MutableStateFlow<LoadingState>(LoadingState.Loading)

}