package com.app.ecarepro.ui.payslip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkPaySlip
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PaySlipViewModel @Inject constructor(
    private val  userRepository: UserRepository
)  : ViewModel() {


    private val payslipStateFlow: MutableStateFlow<NetworkResult<NetworkPaySlip>> = MutableStateFlow(
        NetworkResult.Loading())
    val _payslipStateFlow: StateFlow<NetworkResult<NetworkPaySlip>> = payslipStateFlow

    fun getPayslip(  )=viewModelScope.launch {
        runCatching {
            payslipStateFlow.value = NetworkResult.Loading( )
            userRepository.getPayslip( )
        }.onSuccess {
            payslipStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            payslipStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}