package com.app.ecarepro.ui.report.class_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAttedanceSummary
import com.app.ecarepro.data.network.model.NetworkClassAttendance
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassAttViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val myClassMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMyClass>> = MutableStateFlow(
        NetworkResult.Loading())
    val _myClassStateFlow: StateFlow<NetworkResult<NetworkMyClass>> = myClassMutableStateFlow

    private val classAttendanceMutableStateFlow: MutableStateFlow<NetworkResult<NetworkClassAttendance>> = MutableStateFlow(
        NetworkResult.Loading())
    val classAttStateFlow: StateFlow<NetworkResult<NetworkClassAttendance>> = classAttendanceMutableStateFlow

    fun getMyClass(subID: Int, iD: Int  )=viewModelScope.launch {
        runCatching {
            myClassMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.staffMyClass(subID, iD)
        }.onSuccess {
            myClassMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            myClassMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun getClassAttendance(
        id: String,
        attDate: String
    )=viewModelScope.launch {
        runCatching {
            classAttendanceMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getClassAttendance(id, attDate)
        }.onSuccess {
            classAttendanceMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            classAttendanceMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

}

