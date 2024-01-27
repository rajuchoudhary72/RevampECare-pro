package com.app.ecarepro.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {
    fun verifyUser(username: String, onResponse: (NetworkUserDetailsDto) -> Unit) {
        viewModelScope.launch {
            onResponse(
                userRepository.verifyUser(
                    schoolCode = userDataStore.getSchoolData()?.schoolCode!!,
                    username = username
                )
            )
        }
    }
    fun login(username: String, password: String, onResponse: (LoginResponseDto) -> Unit) {
        viewModelScope.launch {
            onResponse(
                userRepository.login(
                    schoolCode = userDataStore.getSchoolData()?.schoolCode!!,
                    userName = username,
                    password = password
                )
            )
        }
    }


}