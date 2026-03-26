package com.app.ecarepro.feature.profile.profile

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.SavedAccountItem
import com.app.ecarepro.core.domain.model.profile.MyProfileData
import com.app.ecarepro.core.domain.repository.MyProfileRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val repository: MyProfileRepository,
    private val userRepository: UserRepository,
) : BaseViewModel<MyProfileIntent, MyProfileEvent>() {

    private val _uiState = MutableStateFlow(MyProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
        loadSavedAccounts()
    }

    override fun handleIntent(intent: MyProfileIntent) {
        when (intent) {
            MyProfileIntent.OnBackClicked -> sendEvent(MyProfileEvent.NavigateBack)
            MyProfileIntent.OnEditClicked -> sendEvent(MyProfileEvent.NavigateToEdit)
            is MyProfileIntent.ToggleSection -> toggleSection(intent.section)
            MyProfileIntent.ShowSwitchAccount -> _uiState.update { it.copy(showSwitchAccount = true) }
            MyProfileIntent.DismissSwitchAccount -> _uiState.update { it.copy(showSwitchAccount = false) }
            MyProfileIntent.OnLogoutClicked -> _uiState.update { it.copy(showLogoutConfirmation = true) }
            MyProfileIntent.OnLogoutConfirmed -> {
                _uiState.update { it.copy(showLogoutConfirmation = false) }
                logout()
            }
            MyProfileIntent.DismissLogoutConfirmation -> _uiState.update { it.copy(showLogoutConfirmation = false) }
            MyProfileIntent.Retry -> loadProfile()
            is MyProfileIntent.SwitchToAccount -> switchToAccount(intent.localId)
            is MyProfileIntent.RemoveAccount -> removeAccount(intent.localId)
            MyProfileIntent.OnAddAccountClicked -> sendEvent(MyProfileEvent.NavigateToAddAccount)
        }
    }

    private fun toggleSection(section: ProfileSectionType) {
        _uiState.update { state ->
            val current = state.expandedSections
            val updated = if (section in current) current - section else current + section
            state.copy(expandedSections = updated)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.logout()
            sendEvent(MyProfileEvent.NavigateToLogin)
        }
    }

    private fun loadSavedAccounts() {
        viewModelScope.launch {
            val accounts = userRepository.getAllSavedAccounts()
            _uiState.update { it.copy(savedAccounts = accounts) }
        }
    }

    private fun switchToAccount(localId: Int) {
        viewModelScope.launch {
            userRepository.switchToAccount(localId)
            sendEvent(MyProfileEvent.RestartApp)
        }
    }

    private fun removeAccount(localId: Int) {
        viewModelScope.launch {
            userRepository.deleteAccount(localId)
            val accounts = userRepository.getAllSavedAccounts()
            _uiState.update { it.copy(savedAccounts = accounts) }
        }
    }

    private fun loadProfile() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getProfile().collect { result ->
                result
                    .onSuccess { data ->
                        val sections = buildSectionsFor(data)
                        val visibleSections = when (data.userType) {
                            1 -> listOf(
                                ProfileSectionType.PERSONAL_INFO,
                                ProfileSectionType.CONTACT_INFO,
                                ProfileSectionType.IDS,
                                ProfileSectionType.PROFESSIONAL_INFO,
                            )
                            else -> ProfileSectionType.entries.toList()
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                profileData = data,
                                canEditProfile = data.canEditProfile,
                                sectionFields = sections,
                                visibleSections = visibleSections,
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

    private fun buildSectionsFor(data: MyProfileData): Map<ProfileSectionType, List<ProfileFieldItem>> {
        return when (data.userType) {
            1 -> buildStudentSections(data)
            2 -> buildParentSections(data)
            else -> buildStaffSections(data)
        }
    }

    private fun buildStaffSections(data: MyProfileData): Map<ProfileSectionType, List<ProfileFieldItem>> =
        mapOf(
            ProfileSectionType.PERSONAL_INFO to listOf(
                ProfileFieldItem("First Name", data.fName),
                ProfileFieldItem("Middle Name", data.mName),
                ProfileFieldItem("Last Name", data.lName),
                ProfileFieldItem("Gender", data.gender),
                ProfileFieldItem("Date of Birth", data.dob),
                ProfileFieldItem("Date of Joining", data.doj),
                ProfileFieldItem("Marital Status", data.maritalStatus),
                ProfileFieldItem("Anniversary Date", data.doAnniversary),
                ProfileFieldItem("Father / Husband Name", data.fatherHusbandName),
                ProfileFieldItem("Religion", data.religion),
                ProfileFieldItem("Nationality", data.nationality),
                ProfileFieldItem("Blood Group", data.bloodGroup),
            ),
            ProfileSectionType.CONTACT_INFO to listOf(
                ProfileFieldItem("Mobile", data.mobile),
                ProfileFieldItem("Father / Spouse Contact", data.fatherHusbandMob),
                ProfileFieldItem("Alternate Mobile", data.alternateMobile),
                ProfileFieldItem("Emergency Contact", data.emergencyContactNo),
                ProfileFieldItem("Email", data.emailID),
                ProfileFieldItem("Alternate Email", data.alternateEmailID),
                ProfileFieldItem("Address", data.address),
                ProfileFieldItem("Permanent Address", data.permanentAddress),
            ),
            ProfileSectionType.IDS to listOf(
                ProfileFieldItem("Aadhaar Number", data.aadhar),
                ProfileFieldItem("PAN Number", data.pan),
                ProfileFieldItem("CBSE ID", data.cbseId),
                ProfileFieldItem("UAN Number", data.uan),
                ProfileFieldItem("National Teacher ID", data.nationalCode),
            ),
            ProfileSectionType.PROFESSIONAL_INFO to listOf(
                ProfileFieldItem("Designation", data.designation),
                ProfileFieldItem("Role", data.roleName),
            ),
            ProfileSectionType.EDUCATIONAL_QUALIFICATIONS to listOf(
                ProfileFieldItem("Qualification", data.qualification),
            ),
        )

    private fun buildParentSections(data: MyProfileData): Map<ProfileSectionType, List<ProfileFieldItem>> {
        val student = data.studentProfile
        return mapOf(
            ProfileSectionType.PERSONAL_INFO to listOf(
                ProfileFieldItem("Name", data.name),
                ProfileFieldItem("Mobile", data.mobile),
                ProfileFieldItem("Email", data.emailID),
                ProfileFieldItem("Address", data.address),
            ),
            ProfileSectionType.CONTACT_INFO to listOf(
                ProfileFieldItem("Alternate Mobile", data.alternateMobile),
                ProfileFieldItem("Alternate Email", data.alternateEmailID),
                ProfileFieldItem("Emergency Contact", data.emergencyContactNo),
            ),
            ProfileSectionType.IDS to if (student != null) listOf(
                ProfileFieldItem("Ward Name", student.name),
                ProfileFieldItem("Class", student.className),
                ProfileFieldItem("Admission No", student.admissionNo),
                ProfileFieldItem("Date of Birth", student.dob),
                ProfileFieldItem("Blood Group", student.bloodGroup),
                ProfileFieldItem("Religion", student.religion),
                ProfileFieldItem("Aadhaar", student.aadhaarNumber),
            ) else emptyList(),
            ProfileSectionType.PROFESSIONAL_INFO to if (student != null) listOf(
                ProfileFieldItem("Father Name", student.fatherName),
                ProfileFieldItem("Father Mobile", student.fatherMob),
                ProfileFieldItem("Father Email", student.fatherEmail),
                ProfileFieldItem("Father Aadhaar", student.fatherAadhaar),
            ) else emptyList(),
            ProfileSectionType.EDUCATIONAL_QUALIFICATIONS to if (student != null) listOf(
                ProfileFieldItem("Mother Name", student.motherName),
                ProfileFieldItem("Mother Mobile", student.motherMob),
                ProfileFieldItem("Mother Email", student.motherEmail),
                ProfileFieldItem("Mother Aadhaar", student.motherAadhaar),
            ) else emptyList(),
        )
    }

    private fun buildStudentSections(data: MyProfileData): Map<ProfileSectionType, List<ProfileFieldItem>> {
        val student = data.studentProfile
        return mapOf(
            ProfileSectionType.PERSONAL_INFO to listOf(
                ProfileFieldItem("Name", data.name),
                ProfileFieldItem("Gender", data.gender),
                ProfileFieldItem("Admission No", student?.admissionNo.orEmpty()),
                ProfileFieldItem("Class", student?.className.orEmpty()),
                ProfileFieldItem("Date of Birth", student?.dob.orEmpty()),
            ),
            ProfileSectionType.CONTACT_INFO to listOf(
                ProfileFieldItem("Mobile", student?.contactMobile.orEmpty()),
                ProfileFieldItem("Address", student?.address.orEmpty()),
            ),
            ProfileSectionType.IDS to listOf(
                ProfileFieldItem("Aadhaar Number", student?.aadhaarNumber.orEmpty()),
                ProfileFieldItem("PEN Number", data.penNumber),
                ProfileFieldItem("APAAR ID", data.apaarId),
                ProfileFieldItem("SAT Number", data.satNumber),
            ),
            ProfileSectionType.PROFESSIONAL_INFO to listOf(
                ProfileFieldItem("Father Name", student?.fatherName.orEmpty()),
                ProfileFieldItem("Mother Name", student?.motherName.orEmpty()),
                ProfileFieldItem("Father Aadhaar", student?.fatherAadhaar.orEmpty()),
                ProfileFieldItem("Mother Aadhaar", student?.motherAadhaar.orEmpty()),
            ),
        )
    }
}

@Immutable
data class MyProfileUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val profileData: MyProfileData? = null,
    val canEditProfile: Boolean = false,
    val expandedSections: Set<ProfileSectionType> = emptySet(),
    val sectionFields: Map<ProfileSectionType, List<ProfileFieldItem>> = emptyMap(),
    val visibleSections: List<ProfileSectionType> = emptyList(),
    val showSwitchAccount: Boolean = false,
    val savedAccounts: List<SavedAccountItem> = emptyList(),
    val showLogoutConfirmation: Boolean = false,
)

enum class ProfileSectionType(val defaultTitle: String) {
    PERSONAL_INFO("Personal info"),
    CONTACT_INFO("Contact info"),
    IDS("IDs"),
    PROFESSIONAL_INFO("Professional info"),
    EDUCATIONAL_QUALIFICATIONS("Educational qualifications");

    fun titleFor(userType: Int): String = when (this) {
        PERSONAL_INFO -> "Personal info"
        CONTACT_INFO -> "Contact info"
        IDS -> if (userType == 2) "Ward details" else "IDs"
        PROFESSIONAL_INFO -> when (userType) {
            2 -> "Father's details"
            1 -> "Parent info"
            else -> "Professional info"
        }
        EDUCATIONAL_QUALIFICATIONS -> if (userType == 2) "Mother's details" else "Educational qualifications"
    }

    fun icon(): ImageVector = when (this) {
        PERSONAL_INFO -> Icons.Default.Person
        CONTACT_INFO -> Icons.Default.Phone
        IDS -> Icons.Default.Badge
        PROFESSIONAL_INFO -> Icons.Default.Work
        EDUCATIONAL_QUALIFICATIONS -> Icons.Default.School
    }

    fun iconColor(): Color = when (this) {
        PERSONAL_INFO -> Color(0xFF5C6BC0)
        CONTACT_INFO -> Color(0xFF26A69A)
        IDS -> Color(0xFFEF5350)
        PROFESSIONAL_INFO -> Color(0xFF42A5F5)
        EDUCATIONAL_QUALIFICATIONS -> Color(0xFF66BB6A)
    }
}

data class ProfileFieldItem(val label: String, val value: String)

sealed interface MyProfileIntent {
    data object OnBackClicked : MyProfileIntent
    data object OnEditClicked : MyProfileIntent
    data class ToggleSection(val section: ProfileSectionType) : MyProfileIntent
    data object ShowSwitchAccount : MyProfileIntent
    data object DismissSwitchAccount : MyProfileIntent
    data object OnLogoutClicked : MyProfileIntent
    data object OnLogoutConfirmed : MyProfileIntent
    data object DismissLogoutConfirmation : MyProfileIntent
    data object Retry : MyProfileIntent
    data class SwitchToAccount(val localId: Int) : MyProfileIntent
    data class RemoveAccount(val localId: Int) : MyProfileIntent
    data object OnAddAccountClicked : MyProfileIntent
}

sealed interface MyProfileEvent {
    data object NavigateBack : MyProfileEvent
    data object NavigateToEdit : MyProfileEvent
    data object NavigateToLogin : MyProfileEvent
    data object NavigateToAddAccount : MyProfileEvent
    data object RestartApp : MyProfileEvent
}
