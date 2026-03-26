package com.app.ecarepro.feature.message.screens

import androidx.compose.runtime.Immutable
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.personlist.Gender
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonType
import com.app.ecarepro.feature.message.common.updateSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// ============== User Type Enum ==============

enum class MessageUserType(val displayName: String) {
    STUDENT("Student"),
    STAFF("Staff"),
    PARENT("Parent"),
}

// ============== ViewModel ==============

@HiltViewModel
class UsersViewModel @Inject constructor() : BaseViewModel<UsersIntent, UsersEvent>() {

    private val _uiState = MutableStateFlow<UiState<UsersUiState>>(
        UiState.Success(
            UsersUiState(
                selectedUserType = MessageUserType.STUDENT,
                students = sampleStudentUsers,
                staff = sampleStaffUsers,
                parents = sampleParentUsers,
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    override fun handleIntent(intent: UsersIntent) {
        when (intent) {
            is UsersIntent.OnRetry -> { /* TODO: reload from API */ }
            is UsersIntent.OnSortClick -> _uiState.updateSuccess { it.copy(showUserTypeSheet = true) }
            is UsersIntent.OnUserTypeSheetDismiss -> _uiState.updateSuccess { it.copy(showUserTypeSheet = false) }
            is UsersIntent.OnSearchQueryChanged -> _uiState.updateSuccess { it.copy(searchQuery = intent.query) }
            is UsersIntent.OnUserTypeSelected -> _uiState.updateSuccess {
                it.copy(
                    selectedUserType = intent.userType,
                    searchQuery = "",
                    showUserTypeSheet = false,
                )
            }
        }
    }
}

// ============== UI State ==============

@Immutable
data class UsersUiState(
    val selectedUserType: MessageUserType = MessageUserType.STUDENT,
    val searchQuery: String = "",
    val showUserTypeSheet: Boolean = false,
    val students: List<PersonPresentation> = emptyList(),
    val staff: List<PersonPresentation> = emptyList(),
    val parents: List<PersonPresentation> = emptyList(),
) {
    val displayedUsers: List<PersonPresentation>
        get() {
            val list = when (selectedUserType) {
                MessageUserType.STUDENT -> students
                MessageUserType.STAFF -> staff
                MessageUserType.PARENT -> parents
            }
            return if (searchQuery.isBlank()) list
            else list.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
}

// ============== Intents ==============

sealed interface UsersIntent {
    data object OnRetry : UsersIntent
    data object OnSortClick : UsersIntent
    data object OnUserTypeSheetDismiss : UsersIntent
    data class OnSearchQueryChanged(val query: String) : UsersIntent
    data class OnUserTypeSelected(val userType: MessageUserType) : UsersIntent
}

// ============== Events ==============

sealed interface UsersEvent {
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : UsersEvent
}

// ============== Sample Data ==============

internal val sampleStudentUsers = listOf(
    PersonPresentation(
        id = "st1", name = "Aastha Saini, 11 A",
        subtitle = "Admission No: AB123",
        detail = "Roll No: 1",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.STUDENT,
    ),
    PersonPresentation(
        id = "st2", name = "Naina Singh, 11 A",
        subtitle = "Admission No: AB124",
        detail = "Roll No: 2",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.STUDENT,
    ),
    PersonPresentation(
        id = "st3", name = "Aarushi Sharma, 11 A",
        subtitle = "Admission No: AB125",
        detail = "Roll No: 3",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.STUDENT,
    ),
    PersonPresentation(
        id = "st4", name = "Santavna Garg, 11 A",
        subtitle = "Admission No: AB126",
        detail = "Roll No: 4",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.STUDENT,
    ),
    PersonPresentation(
        id = "st5", name = "Pooja Baheti, 11 A",
        subtitle = "Admission No: AB127",
        detail = "Roll No: 5",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.STUDENT,
    ),
    PersonPresentation(
        id = "st6", name = "Ruchi Srivastav, 11 A",
        subtitle = "Admission No: AB128",
        detail = "Roll No: 6",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.STUDENT,
    ),
    PersonPresentation(
        id = "st7", name = "Sakshi Goyal, 11 A",
        subtitle = "Admission No: AB129",
        detail = "Roll No: 7",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.STUDENT,
    ),
)

internal val sampleStaffUsers = listOf(
    PersonPresentation(
        id = "sf1", name = "Mr. Rahul Verma",
        subtitle = "Designation: TGT",
        detail = "DOJ: 01 July 2012",
        profileImageURL = null, gender = Gender.MALE, personType = PersonType.STAFF,
    ),
    PersonPresentation(
        id = "sf2", name = "Ms. Priya Sharma",
        subtitle = "Designation: PGT",
        detail = "DOJ: 15 Aug 2015",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.STAFF,
    ),
    PersonPresentation(
        id = "sf3", name = "Mr. Mohit Kumar",
        subtitle = "Designation: Principal",
        detail = "DOJ: 01 Apr 2010",
        profileImageURL = null, gender = Gender.MALE, personType = PersonType.STAFF,
    ),
    PersonPresentation(
        id = "sf4", name = "Ms. Anjali Verma",
        subtitle = "Designation: Librarian",
        detail = "DOJ: 20 Jan 2018",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.STAFF,
    ),
)

internal val sampleParentUsers = listOf(
    PersonPresentation(
        id = "p1", name = "Meena Saini",
        subtitle = "Parent of: Aastha Saini",
        detail = "Class: 11 A",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.PARENT,
    ),
    PersonPresentation(
        id = "p2", name = "Rajesh Singh",
        subtitle = "Parent of: Naina Singh",
        detail = "Class: 11 A",
        profileImageURL = null, gender = Gender.MALE, personType = PersonType.PARENT,
    ),
    PersonPresentation(
        id = "p3", name = "Sunita Sharma",
        subtitle = "Parent of: Aarushi Sharma",
        detail = "Class: 11 A",
        profileImageURL = null, gender = Gender.FEMALE, personType = PersonType.PARENT,
    ),
    PersonPresentation(
        id = "p4", name = "Vikas Garg",
        subtitle = "Parent of: Santavna Garg",
        detail = "Class: 11 A",
        profileImageURL = null, gender = Gender.MALE, personType = PersonType.PARENT,
    ),
)
