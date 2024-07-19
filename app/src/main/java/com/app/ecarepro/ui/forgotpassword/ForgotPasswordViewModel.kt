package com.app.ecarepro.ui.forgotpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val schoolCode = savedStateHandle.get<String>("schoolCode")
        ?: throw IllegalArgumentException("School code required")

    var userType = 1
    var rcvOn = "mob"

    fun getCredentials(value: String, onResponse: (NetworkUserDetailsDto) -> Unit) {
        viewModelScope.launch {
            onResponse(
                userRepository.getCredentials(
                    userType = userType,
                    rcvOn = rcvOn,
                    schoolCode = schoolCode,
                    email = if (rcvOn == "email") value else null,
                    mobile = if (rcvOn == "mob") value else null
                )
            )
        }
    }

}