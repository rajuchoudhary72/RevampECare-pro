package com.app.ecarepro.ui.assign_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.AssignHouseRequest
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.ClassPromotionModel
import com.app.ecarepro.ui.medicine_issue.MedicineIsuueModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AssignHomeViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val medicineIssueModelMutableStateFlow: MutableStateFlow<NetworkResult<MedicineIsuueModel>> = MutableStateFlow(
        NetworkResult.Loading())
    val leaveHistoryStateFlow: StateFlow<NetworkResult<MedicineIsuueModel>> = medicineIssueModelMutableStateFlow

    private val classList: MutableStateFlow<NetworkResult<ClassPromotionModel>> =
        MutableStateFlow(NetworkResult.Loading())
    val _classList: StateFlow<NetworkResult<ClassPromotionModel>> = classList

    private val studentList: MutableStateFlow<NetworkResult<StudentList>> =
        MutableStateFlow(NetworkResult.Loading())
    val _studentList: StateFlow<NetworkResult<StudentList>> = studentList

private val assignHouse: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(NetworkResult.Loading())
    val _assignHouse: StateFlow<NetworkResult<CommonResponse>> = assignHouse

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
    fun getClassList() = viewModelScope.launch {
        runCatching {
            classList.value = NetworkResult.Loading()
            schoolRepository.getClass()
        }.onSuccess {
            classList.value = NetworkResult.Success(it)
        }.onFailure {
            classList.value = NetworkResult.Error(it.message)
        }

    }
    fun getStudentList(id:String, orderBy:String) = viewModelScope.launch {
        runCatching {
            studentList.value = NetworkResult.Loading()
            schoolRepository.getStudentListToAssignHouse(id,orderBy)
        }.onSuccess {
            studentList.value = NetworkResult.Success(it)
        }.onFailure {
            studentList.value = NetworkResult.Error(it.message)
        }

    }
    fun assignHouse(request:List<AssignHouseRequest>) = viewModelScope.launch {
        runCatching {
            assignHouse.value = NetworkResult.Loading()
            schoolRepository.assignHouse(request)
        }.onSuccess {
            assignHouse.value = NetworkResult.Success(it)
        }.onFailure {
            assignHouse.value = NetworkResult.Error(it.message)
        }

    }
}