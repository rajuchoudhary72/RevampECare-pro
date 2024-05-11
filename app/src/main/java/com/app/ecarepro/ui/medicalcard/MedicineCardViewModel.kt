package com.app.ecarepro.ui.medicalcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse

import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.UpdateMedicalCardRequest
import com.app.ecarepro.ui.medicine_issue.MedicineIsuueModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MedicineCardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    private val medicineCardMutableStateFlow: MutableStateFlow<NetworkResult<MedicalCardResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val medicineCard: StateFlow<NetworkResult<MedicalCardResponse>> = medicineCardMutableStateFlow

    private val updateMedicalCardStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val _updateMedicalCardStateFlow: StateFlow<NetworkResult<CommonResponse>> = updateMedicalCardStateFlow


    fun medicineCard() = viewModelScope.launch {
        runCatching {
            medicineCardMutableStateFlow.value = NetworkResult.Loading()
            schoolRepository.getMedicalCard()
        }.onSuccess {
            medicineCardMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            medicineCardMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun medicineCardUpdate(request: UpdateMedicalCardRequest) = viewModelScope.launch {
        runCatching {
            updateMedicalCardStateFlow.value = NetworkResult.Loading()
            schoolRepository.updateMedicalCard(request)
        }.onSuccess {
            updateMedicalCardStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            updateMedicalCardStateFlow.value = NetworkResult.Error(it.message)
        }
    }


}