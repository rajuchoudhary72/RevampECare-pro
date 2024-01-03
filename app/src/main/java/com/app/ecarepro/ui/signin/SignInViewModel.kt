package com.app.ecarepro.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.VerifyUserDto
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    fun verifyUser(username: String, onResponse: (VerifyUserDto) -> Unit) {
        viewModelScope.launch {
            onResponse(userRepository.verifyUser(schoolCode = "DEMOIN", username = username))
        }
    }


}