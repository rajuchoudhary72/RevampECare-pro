package com.app.ecarepro.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkUser
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    fun insertUser() {
        viewModelScope.launch {
            userRepository.insertUser(
                NetworkUser(
                    id = 1,
                    name = "Raju"
                )
            )
        }
    }
}