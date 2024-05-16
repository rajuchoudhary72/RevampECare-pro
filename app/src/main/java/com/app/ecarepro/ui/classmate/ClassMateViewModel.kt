package com.app.ecarepro.ui.classmate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse

import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.ClassMateResponse
import com.app.ecarepro.model.UpdateMedicalCardRequest
import com.app.ecarepro.ui.medicalcard.medical_class.StudentMedicalCardResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ClassMateViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    private val classMateResponseMutableStateFlow: MutableStateFlow<NetworkResult<ClassMateResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val _classMateResponseMutableStateFlow: StateFlow<NetworkResult<ClassMateResponse>> = classMateResponseMutableStateFlow

    fun  getClassMate()=viewModelScope.launch {
        runCatching {
            classMateResponseMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getClassmates()
        }.onSuccess {
            classMateResponseMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            classMateResponseMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }



}