package com.app.ecarepro.ui.report.student_att_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentAttRepo
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentAttRepoViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val studentAttMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStudentAttRepo>> = MutableStateFlow(
        NetworkResult.Loading())
    val studentAttStateFlow: StateFlow<NetworkResult<NetworkStudentAttRepo>> = studentAttMutableStateFlow


    fun  getStudentAttendance(
        from: String,
        till: String,
        yrID: String,
        iD: String,
    )=viewModelScope.launch {
        runCatching {
            studentAttMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getStudentAttendance(from, till, yrID, iD )
        }.onSuccess {
            studentAttMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            studentAttMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}

