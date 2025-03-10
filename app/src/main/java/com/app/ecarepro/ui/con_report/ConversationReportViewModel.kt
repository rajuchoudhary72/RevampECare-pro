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
import com.app.ecarepro.model.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationReportViewModel @Inject constructor(

    private val messageRepository: MessageRepository
) : ViewModel() {


    var startDate = ""
    var endDate=""
    var isFirst = true
    var cacheListConversationReport = ArrayList<Conversation>()
    var posIndex = 0
    var canDelete=false

    private val convReportMutableStateFlow: MutableStateFlow<NetworkResult<NetworkConversationReport>> = MutableStateFlow(
        NetworkResult.Loading())
    val convReportStateFlow: StateFlow<NetworkResult<NetworkConversationReport>> = convReportMutableStateFlow

    private val convDeleteMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val convDeleteStateFlow: StateFlow<NetworkResult<CommonResponse>> = convDeleteMutableStateFlow




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

    fun deleteConversation(
        id: String,
        device: Int
    )=viewModelScope.launch {
        runCatching {
            convDeleteMutableStateFlow.value = NetworkResult.Loading( )
            messageRepository.deleteConversation(id, device)
        }.onSuccess {
            convDeleteMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            convDeleteMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }



}