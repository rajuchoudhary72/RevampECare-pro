package com.app.ecarepro.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val noticeStateFlow: MutableStateFlow<NetworkResult<NetworkNotice>> = MutableStateFlow(
        NetworkResult.Loading()
    )
    val _noticeStateFlow: StateFlow<NetworkResult<NetworkNotice>> = noticeStateFlow

    private val myClassStateFlow: MutableStateFlow<NetworkResult<NetworkMyClass>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val _myClassStateFlow: StateFlow<NetworkResult<NetworkMyClass>> = myClassStateFlow

    fun getNotice(pg: Int, classID: Int, isClassNotice: Boolean) = viewModelScope.launch {
        runCatching {
            noticeStateFlow.value = NetworkResult.Loading()
            schoolRepository.getNotice(pg, classID, isClassNotice)
        }.onSuccess {
            noticeStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            noticeStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun getMyClass(subID: Int, iD: Int) = viewModelScope.launch {
        runCatching {
            myClassStateFlow.value = NetworkResult.Loading()
            userRepository.staffMyClass(subID, iD)
        }.onSuccess {
            myClassStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            myClassStateFlow.value = NetworkResult.Error(it.message)
        }
    }


}