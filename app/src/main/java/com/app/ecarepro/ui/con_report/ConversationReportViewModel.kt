package com.app.ecarepro.ui.con_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkConversationReport
import com.app.ecarepro.data.network.model.NetworkLeaveListStatus
 import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationReportViewModel @Inject constructor(

    private val messageRepository: MessageRepository
) : ViewModel() {


    private val convReportMutableStateFlow: MutableStateFlow<NetworkResult<NetworkConversationReport>> = MutableStateFlow(
        NetworkResult.Loading())
    val convReportStateFlow: StateFlow<NetworkResult<NetworkConversationReport>> = convReportMutableStateFlow



    fun getConversationReport(
        pg: Int,
        fromDate: String? = null,
        tillDate: String? = null,
    )=viewModelScope.launch {
        runCatching {
            convReportMutableStateFlow.value = NetworkResult.Loading( )
            messageRepository.getConversationReport(pg, fromDate, tillDate)
        }.onSuccess {
            convReportMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            convReportMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }



}