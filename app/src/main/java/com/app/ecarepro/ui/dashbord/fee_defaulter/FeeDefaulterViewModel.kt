package com.app.ecarepro.ui.dashbord.fee_defaulter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkFeeDefaulter
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkWingReport
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeeDefaulterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val feeDefaulterMutableStateFlow: MutableStateFlow<NetworkResult<NetworkFeeDefaulter>> = MutableStateFlow(
        NetworkResult.Loading())
    val  feeDefaulterStateFlow: StateFlow<NetworkResult<NetworkFeeDefaulter>> = feeDefaulterMutableStateFlow


    fun  getFeeDefaulters(
        feeTypeId: Int?,
        installIds: Int?
    )=viewModelScope.launch {
        runCatching {
            feeDefaulterMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getFeeDefaulters( feeTypeId, installIds)
        }.onSuccess {
            feeDefaulterMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            feeDefaulterMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


}