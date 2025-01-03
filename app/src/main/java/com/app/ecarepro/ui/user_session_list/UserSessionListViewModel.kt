package com.app.ecarepro.ui.user_session_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSmsMsgReport
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.network.model.NetworkWingReport
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.NetworkUserSessionsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSessionListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val sessionsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkUserSessionsResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val  sessionsStateFlow: StateFlow<NetworkResult<NetworkUserSessionsResponse>> = sessionsMutableStateFlow

    private val removeSessionMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val  removeSessionStateFlow: StateFlow<NetworkResult<CommonResponse>> = removeSessionMutableStateFlow




    fun  activeSessionsList()=viewModelScope.launch {
        runCatching {
            sessionsMutableStateFlow.value = NetworkResult.Loading()
            userRepository.activeSessions()
        }.onSuccess {
            sessionsMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            sessionsMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


    fun  removeSession(
        sessionID: String?,
    )=viewModelScope.launch {
        runCatching {
            removeSessionMutableStateFlow.value = NetworkResult.Loading()
            userRepository.removeSession(sessionID )
        }.onSuccess {
            removeSessionMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            removeSessionMutableStateFlow.value = NetworkResult.Error(it.message)
        }



    }




}

