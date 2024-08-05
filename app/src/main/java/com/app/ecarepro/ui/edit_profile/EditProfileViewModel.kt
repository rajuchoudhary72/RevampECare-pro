package com.app.ecarepro.ui.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkEditProfile

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
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    private val editProfileMutableStateFlow: MutableStateFlow<NetworkResult<NetworkEditProfile>> = MutableStateFlow(
        NetworkResult.Loading())
    val editProfileStateFlow: StateFlow<NetworkResult<NetworkEditProfile>> = editProfileMutableStateFlow

    fun  getUserProfileEdit(
        edit: Boolean
    )=viewModelScope.launch {
        runCatching {
            editProfileMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getUserProfileEdit(edit)
        }.onSuccess {
            editProfileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            editProfileMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }



}