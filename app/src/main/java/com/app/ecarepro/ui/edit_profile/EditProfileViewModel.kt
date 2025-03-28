package com.app.ecarepro.ui.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkEditProfile

import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.network.model.NetworkTransportEditProfile
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.ClassMateResponse
import com.app.ecarepro.model.UpdateMedicalCardRequest
import com.app.ecarepro.ui.edit_profile.model.Profile
import com.app.ecarepro.ui.edit_profile.model.update_profile.UpdateProfileModel
import com.app.ecarepro.ui.edit_profile.model.update_profile.UpdateTransportProfileModel
import com.app.ecarepro.ui.medicalcard.medical_class.StudentMedicalCardResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val editProfileMutableStateFlow: MutableStateFlow<NetworkResult<NetworkEditProfile>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val editProfileStateFlow: StateFlow<NetworkResult<NetworkEditProfile>> =
        editProfileMutableStateFlow

    private val updateParentProfileMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val updateParentProfileStateFlow: StateFlow<NetworkResult<CommonResponse>> = updateParentProfileMutableStateFlow

    private val updateTransportProfileMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val updateTransportProfileStateFlow: StateFlow<NetworkResult<CommonResponse>> = updateTransportProfileMutableStateFlow

    private val editTransportProfileMutableStateFlow: MutableStateFlow<NetworkResult<NetworkTransportEditProfile>> =
        MutableStateFlow(NetworkResult.Loading())
    val editTransportProfileStateFlow: StateFlow<NetworkResult<NetworkTransportEditProfile>> = editTransportProfileMutableStateFlow


    fun getUserProfileEdit(
        edit: Boolean
    ) = viewModelScope.launch {
        runCatching {
            editProfileMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getUserProfileEdit(edit)
        }.onSuccess {
            editProfileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            editProfileMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun getUserTransportProfile(
    ) = viewModelScope.launch {
        runCatching {
            editTransportProfileMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getUserTransportProfile()
        }.onSuccess {
            editTransportProfileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            editTransportProfileMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun updateTransportProfile(
        request: UpdateTransportProfileModel
    ) = viewModelScope.launch {
        runCatching {
            updateTransportProfileMutableStateFlow.value = NetworkResult.Loading()
            userRepository.updateTransportProfile(request)
        }.onSuccess {
            updateTransportProfileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            updateTransportProfileMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun updateParentProfile(
        request: UpdateProfileModel
    ) = viewModelScope.launch {
        runCatching {
            updateParentProfileMutableStateFlow.value = NetworkResult.Loading()
            userRepository.updateParentProfile(request)
        }.onSuccess {
            updateParentProfileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            updateParentProfileMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }
}