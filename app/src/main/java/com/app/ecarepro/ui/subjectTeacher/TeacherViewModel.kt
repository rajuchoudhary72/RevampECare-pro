package com.app.ecarepro.ui.subjectTeacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.StudentTeacherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val studentTeacherResponseMutableStateFlow: MutableStateFlow<NetworkResult<StudentTeacherResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val _studentTeacherResponseMutableStateFlow: StateFlow<NetworkResult<StudentTeacherResponse>> = studentTeacherResponseMutableStateFlow

    fun  getSubjectTeacher()=viewModelScope.launch {
        runCatching {
            studentTeacherResponseMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getStudentTeachers()
        }.onSuccess {
            studentTeacherResponseMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            studentTeacherResponseMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}