package com.app.ecarepro.ui.changeusername

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.ecarepro.data.network.model.ChangeUserNameRequestDto

@HiltViewModel
class ChangeUsernameViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val currentUsername = MutableStateFlow("")
    val newUsername = MutableStateFlow("")

    val isUsernameValid = combine(
        flow = currentUsername,
        flow2 = newUsername,
    ) { current, new ->
        current.isNotEmpty() && new.isNotEmpty()
    }.asLiveData()


    fun changeUsername(result: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            userRepository
                .changeUserName(
                    ChangeUserNameRequestDto(
                        currentUsername = currentUsername.value,
                        newUsername = newUsername.value
                    )
                )
                .collectLatest { result ->
                    if (result.isSuccess) {
                        result(true, result.getOrNull()?.message ?: "Success")
                    } else {
                        result(false, result.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE)
                    }
                }
        }
    }
}