package com.app.ecarepro.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkNoticDetails
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeDetailsViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository

) : ViewModel() {

    private val noticeStateFlow: MutableStateFlow<NetworkResult<NetworkNoticDetails>> = MutableStateFlow(
        NetworkResult.Loading())
    val _noticeStateFlow: StateFlow<NetworkResult<NetworkNoticDetails>> = noticeStateFlow
    fun getNoticeDTL(id:String) = viewModelScope.launch {

        noticeStateFlow.value = NetworkResult.Loading( )

        runCatching {
            noticeStateFlow.value = NetworkResult.Loading()
            schoolRepository.getNoticeDTL(id)

        }.onSuccess {
            noticeStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            noticeStateFlow.value = NetworkResult.Error(it.message)
        }
     }

}