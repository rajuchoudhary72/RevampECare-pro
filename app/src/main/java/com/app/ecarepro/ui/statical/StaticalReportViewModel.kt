package com.app.ecarepro.ui.statical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.appuserreport.AppUserReportResponse
import com.app.ecarepro.ui.appuserreport.AppUserWebResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaticalReportViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val staticGraphResponseMutableStateFlow: MutableStateFlow<NetworkResult<StaticGraphResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val staticGraphResponseStateFlow: StateFlow<NetworkResult<StaticGraphResponse>> =
        staticGraphResponseMutableStateFlow

 private val appUserReportResponseMutableStateFlow: MutableStateFlow<NetworkResult<AppUserReportResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val appUserReportResponseStateFlow: StateFlow<NetworkResult<AppUserReportResponse>> =
        appUserReportResponseMutableStateFlow

    fun statistical() = viewModelScope.launch {
        runCatching {
            staticGraphResponseMutableStateFlow.value = NetworkResult.Loading()
            userRepository.statistical()
        }.onSuccess {
            staticGraphResponseMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            staticGraphResponseMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


fun appUserReportResponse() = viewModelScope.launch {
        runCatching {
            appUserReportResponseMutableStateFlow.value = NetworkResult.Loading()
            userRepository.appUserReportResponse()
        }.onSuccess {
            appUserReportResponseMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            appUserReportResponseMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    private val appUserWebResponseMutableStateFlow: MutableStateFlow<NetworkResult<AppUserWebResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val appUserWebResponseStateFlow: StateFlow<NetworkResult<AppUserWebResponse>> =
        appUserWebResponseMutableStateFlow

    fun appUserReportWebResponse(userType:String) = viewModelScope.launch {
        runCatching {
            appUserWebResponseMutableStateFlow.value = NetworkResult.Loading()
            userRepository.appUserReportWevResponse(userType)
        }.onSuccess {
            appUserWebResponseMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            appUserWebResponseMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }
}