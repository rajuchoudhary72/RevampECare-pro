package com.app.ecarepro.ui.update_parent_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkAssignRollNo
import com.app.ecarepro.data.network.model.NetworkClassTeacherOf
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.StudentPhotoUploadModel
import com.app.ecarepro.data.network.model.post_roll_no.AssignRollNoBodyItem
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.studentId.ParentPhotoRequest
import com.app.ecarepro.ui.studentId.StudentIDRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class UpdateParentPhotoViewModel @Inject constructor(
     private val  userRepository: UserRepository
) : ViewModel() {


    private val classTeacherOfMutableStateFlow: MutableStateFlow<NetworkResult<NetworkClassTeacherOf>> = MutableStateFlow(
        NetworkResult.Loading())
    val classTeacherOfStateFlow: StateFlow<NetworkResult<NetworkClassTeacherOf>> = classTeacherOfMutableStateFlow

    private val assignRollNoMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAssignRollNo>> = MutableStateFlow(
        NetworkResult.Loading())
    val assignRollNoStateFlow: StateFlow<NetworkResult<NetworkAssignRollNo>> = assignRollNoMutableStateFlow

    private val uploadStudentPhotoMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val uploadStudentPhotoStateFlow: StateFlow<NetworkResult<CommonResponse>> = uploadStudentPhotoMutableStateFlow



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

    fun uploadStudentPhoto(
        request: ParentPhotoRequest
    )=viewModelScope.launch {
        runCatching {
            uploadStudentPhotoMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.uploadParentPhoto(request )
        }.onSuccess {
            uploadStudentPhotoMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            uploadStudentPhotoMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }


}