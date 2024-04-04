package com.app.ecarepro.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Profile
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userRepository: UserRepository,
    userDataStore: UserDataStore
) : ViewModel() {

    var userType: Int = 0

    val uiState = userRepository
        .getUserProfile()
        .map { result ->
            if (result.isSuccess) {
                ProfileUiState.Success(result.getOrNull()!!)
            } else {
                ProfileUiState.Error(
                    result.exceptionOrNull() ?: IllegalArgumentException(
                        UNKNOWN_ERROR_MESSAGE
                    )
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = ProfileUiState.Loading,
            started = SharingStarted.WhileSubscribed(300)
        )

    init {
        viewModelScope.launch {
            userType = userDataStore.getUser().userType
        }
    }

    fun isParent() = userType == 2
    fun isStudent() = userType == 1
}

sealed interface ProfileUiState {
    object Loading : ProfileUiState

    data class Success(
        val profile: Profile
    ) : ProfileUiState

    data class Error(
        val error: Throwable
    ) : ProfileUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}