package com.app.ecarepro.ui.reportCard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkReportCardDetails
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ReportCardDetailsNavHostViewModel @Inject constructor(
    private val  userRepository: UserRepository
) : ViewModel() {

    private val reportCardDetailsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkReportCardDetails>> = MutableStateFlow(
        NetworkResult.Loading())
    val reportCardDetailsStateFlow: StateFlow<NetworkResult<NetworkReportCardDetails>> = reportCardDetailsMutableStateFlow

    fun reportCardDTL(
        stID: Int
    )=viewModelScope.launch {
        runCatching {
            reportCardDetailsMutableStateFlow.value = NetworkResult.Loading()
            userRepository.reportCardDTL(stID)
        }.onSuccess {
            reportCardDetailsMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            reportCardDetailsMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}