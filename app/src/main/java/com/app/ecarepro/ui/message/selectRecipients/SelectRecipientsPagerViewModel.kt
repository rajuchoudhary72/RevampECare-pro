package com.app.ecarepro.ui.message.selectRecipients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.ClassContact
import com.app.ecarepro.data.network.model.Contact
import com.app.ecarepro.data.network.model.StaffType
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.model.RecipientsType
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SelectRecipientsPagerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val messageRepository: MessageRepository
) : ViewModel() {

    val recipientsType = savedStateHandle.getStateFlow(
        SelectRecipientPagerFragment.RECIPIENTS_TYPE,
        RecipientsType.PARENTS
    )

    private var selectedClassId: Int? = null


    val staffTypes: Flow<Result<List<StaffType>>> =
        recipientsType
            .filter { recipientsType -> recipientsType == RecipientsType.STAFFS }
            .flatMapLatest {
                messageRepository.getStaffTypes()
            }


    val uiState = recipientsType.flatMapLatest { recipientsType ->
        when (recipientsType) {
            RecipientsType.STAFFS -> {
                getStaffContacts()
            }

            else -> {
                getClassContacts(recipientsType)
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            initialValue = SelectRecipientsUiState.Loading,
            started = SharingStarted.WhileSubscribed(500)
        )


    private fun getStaffContacts(): Flow<SelectRecipientsUiState> {
        return messageRepository.getStaffContacts().map { result ->
            if (result.isSuccess) {
                val response = result.getOrNull()
                if (response?.contacts.isNullOrEmpty()) {
                    SelectRecipientsUiState.EmptyContact
                } else {
                    SelectRecipientsUiState.StaffContact(
                        contacts = response?.contacts ?: emptyList()
                    )
                }
            } else {
                SelectRecipientsUiState.Error(
                    error = result.exceptionOrNull() ?: IllegalArgumentException(
                        UNKNOWN_ERROR_MESSAGE
                    )
                )
            }
        }
    }

    private fun getClassContacts(recipientsType: RecipientsType): Flow<SelectRecipientsUiState> {
        return messageRepository.getClassContacts(
            if (recipientsType == RecipientsType.PARENTS)
                2
            else
                1,
            2
        ).map { result ->
            if (result.isSuccess) {
                val classContacts = result.getOrNull()
                if (classContacts.isNullOrEmpty()) {
                    SelectRecipientsUiState.EmptyContact
                } else {
                    if (recipientsType == RecipientsType.PARENTS) {
                        SelectRecipientsUiState.ParentContact(
                            contacts = classContacts
                        )
                    } else {
                        SelectRecipientsUiState.StudentContact(
                            contacts = classContacts,
                            selectedClassID = 28
                        )
                    }
                }
            } else {
                SelectRecipientsUiState.Error(
                    error = result.exceptionOrNull() ?: IllegalArgumentException(
                        UNKNOWN_ERROR_MESSAGE
                    )
                )
            }
        }
    }

    fun setSelectedClassId(classId: Int) {
        selectedClassId = classId
    }

    fun getSelectedClassId() = selectedClassId
}


sealed interface SelectRecipientsUiState {

    object Loading : SelectRecipientsUiState

    object EmptyContact : SelectRecipientsUiState

    data class StudentContact(
        val selectedClassID: Int? = null,
        val contacts: List<ClassContact>
    ) : SelectRecipientsUiState

    data class ParentContact(
        val selectedClassID: Int? = null,
        val contacts: List<ClassContact>
    ) : SelectRecipientsUiState

    data class StaffContact(
        val contacts: List<Contact>
    ) : SelectRecipientsUiState

    data class Error(
        val error: Throwable
    ) : SelectRecipientsUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}