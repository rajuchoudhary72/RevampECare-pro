package com.app.ecarepro.ui.birthday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkBirthday
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BirthdayViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val birthdayMutableStateFlow: MutableStateFlow<NetworkResult<NetworkBirthday>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val birthdayStateFlow: StateFlow<NetworkResult<NetworkBirthday>> = birthdayMutableStateFlow

    fun birthday(
        userType: Int,
        rptType: Int,
        monthNo: Int,
        date: String,
    ) = viewModelScope.launch {
        runCatching {
            birthdayMutableStateFlow.value = NetworkResult.Loading()
            userRepository.birthday(userType, rptType, monthNo, date)
        }.onSuccess {
            birthdayMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            birthdayMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}