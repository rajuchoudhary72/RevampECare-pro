package com.app.ecarepro.ui.assign_roll_no

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkAssignRollNo
import com.app.ecarepro.data.network.model.NetworkClassTeacherOf
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.post_roll_no.AssignRollNoBodyItem
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AssignRollNoViewModel @Inject constructor(
     private val  userRepository: UserRepository
) : ViewModel() {


    private val classTeacherOfMutableStateFlow: MutableStateFlow<NetworkResult<NetworkClassTeacherOf>> = MutableStateFlow(
        NetworkResult.Loading())
    val classTeacherOfStateFlow: StateFlow<NetworkResult<NetworkClassTeacherOf>> = classTeacherOfMutableStateFlow

    private val assignRollNoMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAssignRollNo>> = MutableStateFlow(
        NetworkResult.Loading())
    val assignRollNoStateFlow: StateFlow<NetworkResult<NetworkAssignRollNo>> = assignRollNoMutableStateFlow

    private val requestAssignRollNoMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val requestAssignRollNoStateFlow: StateFlow<NetworkResult<CommonResponse>> = requestAssignRollNoMutableStateFlow



    fun getClassTeacherOf( )=viewModelScope.launch {
          runCatching {
              classTeacherOfMutableStateFlow.value =NetworkResult.Loading( )
              userRepository.getClassTeacherOf( )
          }.onSuccess {
              classTeacherOfMutableStateFlow.value =NetworkResult.Success(it)
          }.onFailure {
              classTeacherOfMutableStateFlow.value = NetworkResult.Error(it.message)
          }
      }

    fun getStudentListToAssignRollNo(
        iD: String,
        orderby: Int
    )=viewModelScope.launch {
        runCatching {
            assignRollNoMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getStudentListToAssignRollNo(iD, orderby )
        }.onSuccess {
            assignRollNoMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            assignRollNoMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun assignRollNumber(
        request: List<AssignRollNoBodyItem>
    )=viewModelScope.launch {
        runCatching {
            requestAssignRollNoMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.assignRollNumber(request )
        }.onSuccess {
            requestAssignRollNoMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            requestAssignRollNoMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }


}