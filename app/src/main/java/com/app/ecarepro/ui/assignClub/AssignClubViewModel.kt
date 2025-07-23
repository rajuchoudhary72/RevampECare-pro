package com.app.ecarepro.ui.assignClub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
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
class AssignClubViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val medicineIssueModelMutableStateFlow: MutableStateFlow<NetworkResult<MedicineIsuueModel>> = MutableStateFlow(
        NetworkResult.Loading())
    val leaveHistoryStateFlow: StateFlow<NetworkResult<MedicineIsuueModel>> = medicineIssueModelMutableStateFlow

    private val classList: MutableStateFlow<NetworkResult<ClassPromotionModel>> =
        MutableStateFlow(NetworkResult.Loading())
    val _classList: StateFlow<NetworkResult<ClassPromotionModel>> = classList

    private val studentList: MutableStateFlow<NetworkResult<StudentListAssignClub>> =
        MutableStateFlow(NetworkResult.Loading())
    val _studentList: StateFlow<NetworkResult<StudentListAssignClub>> = studentList

private val assignClub: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(NetworkResult.Loading())
    val _assignClub: StateFlow<NetworkResult<CommonResponse>> = assignClub


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
            schoolRepository.getStudentListToAssignClub(id,orderBy)
        }.onSuccess {
            studentList.value = NetworkResult.Success(it)
        }.onFailure {
            studentList.value = NetworkResult.Error(it.message)
        }

    }
    fun assignClub(request:List<AssignClubRequest>) = viewModelScope.launch {
        runCatching {
            assignClub.value = NetworkResult.Loading()
            schoolRepository.assignClub(request)
        }.onSuccess {
            assignClub.value = NetworkResult.Success(it)
        }.onFailure {
            assignClub.value = NetworkResult.Error(it.message)
        }

    }
}