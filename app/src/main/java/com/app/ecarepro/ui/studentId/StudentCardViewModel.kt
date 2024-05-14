package com.app.ecarepro.ui.studentId

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.UpdateMedicalCardRequest
import com.app.ecarepro.ui.medicalcard.MedicalCardResponse
import com.app.ecarepro.ui.medicalcard.medical_class.StudentMedicalCardResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StudentCardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val schoolRepository: SchoolRepository
) : ViewModel() {



    private val studentCardResponse: MutableStateFlow<NetworkResult<StudentCardResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val _studentCardResponse: StateFlow<NetworkResult<StudentCardResponse>> = studentCardResponse
    fun  getMedicalCard()=viewModelScope.launch {
        runCatching {
            studentCardResponse.value = NetworkResult.Loading()
            userRepository.getStudentIDCard()
        }.onSuccess {
            studentCardResponse.value = NetworkResult.Success(it)
        }.onFailure {
            studentCardResponse.value = NetworkResult.Error(it.message)
        }

    }
}