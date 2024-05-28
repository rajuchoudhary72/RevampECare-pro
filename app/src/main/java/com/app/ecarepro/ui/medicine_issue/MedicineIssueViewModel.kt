package com.app.ecarepro.ui.medicine_issue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkLeaveListStatus
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MedicineIssueViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val medicineIssueModelMutableStateFlow: MutableStateFlow<NetworkResult<MedicineIsuueModel>> = MutableStateFlow(
        NetworkResult.Loading())
    val leaveHistoryStateFlow: StateFlow<NetworkResult<MedicineIsuueModel>> = medicineIssueModelMutableStateFlow



    fun medicineIssue(  )=viewModelScope.launch {
        runCatching {
            medicineIssueModelMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.medicineIsuueModel( )
        }.onSuccess {
            medicineIssueModelMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            medicineIssueModelMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }


}