package com.app.ecarepro.feature.profile.edit_profile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.profile.MyProfileData
import com.app.ecarepro.core.domain.model.profile.StaffProfileUpdateRequest
import com.app.ecarepro.core.domain.repository.MyProfileRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: MyProfileRepository,
) : BaseViewModel<EditProfileIntent, EditProfileEvent>() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    private var originalData: MyProfileData? = null

    init {
        loadProfile()
    }

    override fun handleIntent(intent: EditProfileIntent) {
        when (intent) {
            EditProfileIntent.OnBackClicked -> sendEvent(EditProfileEvent.NavigateBack)
            EditProfileIntent.SaveChanges -> saveChanges()
            is EditProfileIntent.OnFirstNameChanged -> _uiState.update { it.copy(fName = intent.value) }
            is EditProfileIntent.OnMiddleNameChanged -> _uiState.update { it.copy(mName = intent.value) }
            is EditProfileIntent.OnLastNameChanged -> _uiState.update { it.copy(lName = intent.value) }
            is EditProfileIntent.OnDobChanged -> _uiState.update { it.copy(dob = intent.value) }
            is EditProfileIntent.OnAnniversaryChanged -> _uiState.update { it.copy(doAnniversary = intent.value) }
            is EditProfileIntent.OnMaritalStatusChanged -> _uiState.update { it.copy(maritalStatus = intent.value) }
            is EditProfileIntent.OnBloodGroupChanged -> _uiState.update { it.copy(bloodGroup = intent.value) }
            is EditProfileIntent.OnReligionChanged -> _uiState.update { it.copy(religion = intent.value) }
            is EditProfileIntent.OnNationalityChanged -> _uiState.update { it.copy(nationality = intent.value) }
            is EditProfileIntent.OnFatherHusbandNameChanged -> _uiState.update { it.copy(fatherHusbandName = intent.value) }
            is EditProfileIntent.OnFatherHusbandMobChanged -> _uiState.update { it.copy(fatherHusbandMob = intent.value) }
            is EditProfileIntent.OnMobileChanged -> _uiState.update { it.copy(mobile = intent.value) }
            is EditProfileIntent.OnAlternateMobileChanged -> _uiState.update { it.copy(alternateMobile = intent.value) }
            is EditProfileIntent.OnEmergencyContactChanged -> _uiState.update { it.copy(emergencyContact = intent.value) }
            is EditProfileIntent.OnEmailChanged -> _uiState.update { it.copy(email = intent.value) }
            is EditProfileIntent.OnAlternateEmailChanged -> _uiState.update { it.copy(alternateEmail = intent.value) }
            is EditProfileIntent.OnAddressChanged -> _uiState.update { it.copy(address = intent.value) }
            is EditProfileIntent.OnPermanentAddressChanged -> _uiState.update { it.copy(permanentAddress = intent.value) }
            is EditProfileIntent.OnAadhaarChanged -> _uiState.update { it.copy(aadhaar = intent.value) }
            is EditProfileIntent.OnPanChanged -> _uiState.update { it.copy(pan = intent.value) }
            is EditProfileIntent.OnCbseIdChanged -> _uiState.update { it.copy(cbseId = intent.value) }
            is EditProfileIntent.OnUanChanged -> _uiState.update { it.copy(uan = intent.value) }
            is EditProfileIntent.OnNationalCodeChanged -> _uiState.update { it.copy(nationalCode = intent.value) }
            is EditProfileIntent.OnQualificationChanged -> _uiState.update { it.copy(qualification = intent.value) }
        }
    }

    private fun loadProfile() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getProfile().collect { result ->
                result
                    .onSuccess { data ->
                        originalData = data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                userType = data.userType,
                                fName = data.fName,
                                mName = data.mName,
                                lName = data.lName,
                                dob = data.dob,
                                doAnniversary = data.doAnniversary,
                                maritalStatus = data.maritalStatus,
                                bloodGroup = data.bloodGroup,
                                religion = data.religion,
                                nationality = data.nationality,
                                fatherHusbandName = data.fatherHusbandName,
                                fatherHusbandMob = data.fatherHusbandMob,
                                mobile = data.mobile,
                                alternateMobile = data.alternateMobile,
                                emergencyContact = data.emergencyContactNo,
                                email = data.emailID,
                                alternateEmail = data.alternateEmailID,
                                address = data.address,
                                permanentAddress = data.permanentAddress,
                                aadhaar = data.aadhar,
                                pan = data.pan,
                                cbseId = data.cbseId,
                                uan = data.uan,
                                nationalCode = data.nationalCode,
                                qualification = data.qualification,
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to load profile",
                            )
                        }
                    }
            }
        }
    }

    private fun saveChanges() {
        val state = _uiState.value
        val original = originalData ?: return
        if (state.isSaving) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            when (state.userType) {
                2 -> saveParentProfile(state)
                else -> saveStaffProfile(state, original)
            }
        }
    }

    private suspend fun saveParentProfile(state: EditProfileUiState) {
        repository.updateParentProfile(
            mobile = state.mobile,
            email = state.email,
            address = state.address,
        ).collect { result ->
            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    sendEvent(EditProfileEvent.ShowMessage(SnackbarMessage("Profile updated successfully", MessageType.SUCCESS)))
                    sendEvent(EditProfileEvent.NavigateBack)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    sendEvent(EditProfileEvent.ShowMessage(SnackbarMessage(error.message ?: "Failed to save", MessageType.ERROR)))
                }
        }
    }

    private suspend fun saveStaffProfile(state: EditProfileUiState, original: MyProfileData) {
        val request = StaffProfileUpdateRequest(
            fName = state.fName,
            mName = state.mName,
            lName = state.lName,
            mobile = state.mobile,
            emailID = state.email,
            alternateEmailID = state.alternateEmail,
            alternateMobile = state.alternateMobile,
            emergencyContactNo = state.emergencyContact,
            fatherHusbandName = state.fatherHusbandName,
            fatherHusbandMob = state.fatherHusbandMob,
            address = state.address,
            permanentAddress = state.permanentAddress,
            qualification = state.qualification,
            aadhar = state.aadhaar,
            pan = state.pan,
            cbseId = state.cbseId,
            uan = state.uan,
            nationalCode = state.nationalCode,
            dob = state.dob,
            doAnniversary = state.doAnniversary,
            isMaritalStatusChanged = state.maritalStatus != original.maritalStatus,
            isBloodGroupChanged = state.bloodGroup != original.bloodGroup,
            isReligionChanged = state.religion != original.religion,
            isNationalityChanged = state.nationality != original.nationality,
        )
        repository.sendStaffProfileRequest(request).collect { result ->
            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    sendEvent(EditProfileEvent.ShowMessage(SnackbarMessage("Profile update request submitted", MessageType.SUCCESS)))
                    sendEvent(EditProfileEvent.NavigateBack)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    sendEvent(EditProfileEvent.ShowMessage(SnackbarMessage(error.message ?: "Failed to save", MessageType.ERROR)))
                }
        }
    }
}

@Immutable
data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val userType: Int = 0,
    // Personal
    val fName: String = "",
    val mName: String = "",
    val lName: String = "",
    val dob: String = "",
    val doAnniversary: String = "",
    val maritalStatus: String = "",
    val bloodGroup: String = "",
    val religion: String = "",
    val nationality: String = "",
    val fatherHusbandName: String = "",
    // Contact
    val mobile: String = "",
    val alternateMobile: String = "",
    val emergencyContact: String = "",
    val email: String = "",
    val alternateEmail: String = "",
    val address: String = "",
    val permanentAddress: String = "",
    val fatherHusbandMob: String = "",
    // IDs
    val aadhaar: String = "",
    val pan: String = "",
    val cbseId: String = "",
    val uan: String = "",
    val nationalCode: String = "",
    // Educational
    val qualification: String = "",
)

sealed interface EditProfileIntent {
    data object OnBackClicked : EditProfileIntent
    data object SaveChanges : EditProfileIntent
    data class OnFirstNameChanged(val value: String) : EditProfileIntent
    data class OnMiddleNameChanged(val value: String) : EditProfileIntent
    data class OnLastNameChanged(val value: String) : EditProfileIntent
    data class OnDobChanged(val value: String) : EditProfileIntent
    data class OnAnniversaryChanged(val value: String) : EditProfileIntent
    data class OnMaritalStatusChanged(val value: String) : EditProfileIntent
    data class OnBloodGroupChanged(val value: String) : EditProfileIntent
    data class OnReligionChanged(val value: String) : EditProfileIntent
    data class OnNationalityChanged(val value: String) : EditProfileIntent
    data class OnFatherHusbandNameChanged(val value: String) : EditProfileIntent
    data class OnFatherHusbandMobChanged(val value: String) : EditProfileIntent
    data class OnMobileChanged(val value: String) : EditProfileIntent
    data class OnAlternateMobileChanged(val value: String) : EditProfileIntent
    data class OnEmergencyContactChanged(val value: String) : EditProfileIntent
    data class OnEmailChanged(val value: String) : EditProfileIntent
    data class OnAlternateEmailChanged(val value: String) : EditProfileIntent
    data class OnAddressChanged(val value: String) : EditProfileIntent
    data class OnPermanentAddressChanged(val value: String) : EditProfileIntent
    data class OnAadhaarChanged(val value: String) : EditProfileIntent
    data class OnPanChanged(val value: String) : EditProfileIntent
    data class OnCbseIdChanged(val value: String) : EditProfileIntent
    data class OnUanChanged(val value: String) : EditProfileIntent
    data class OnNationalCodeChanged(val value: String) : EditProfileIntent
    data class OnQualificationChanged(val value: String) : EditProfileIntent
}

sealed interface EditProfileEvent {
    data object NavigateBack : EditProfileEvent
    data class ShowMessage(val message: SnackbarMessage) : EditProfileEvent
}
