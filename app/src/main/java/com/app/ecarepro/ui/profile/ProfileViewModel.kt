package com.app.ecarepro.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.Profile
import com.app.ecarepro.data.network.model.UploadPhotoRequest
import com.app.ecarepro.data.network.model.asUserEntity
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    userDataStore: UserDataStore,
    private val userDatabase: UserDatabase
) : ViewModel() {

    var userType: Int = 0

    val uiState = combine(
        flow = userDataStore.getUsersFlow(),
        flow2 = userRepository.getUserProfile(),
        flow3 = userDataStore.getCurrentUserIdAsFlow()
    ) { users, profile, userId ->
        Triple(users, profile, userId)
    }.map { (users, profile, userId) ->
        if (profile.isSuccess) {
            ProfileUiState.Success(
                profile = profile.getOrNull()!!,
                users = users,
                currentUserId = userId!!
            )
        } else {
            ProfileUiState.Error(
                profile.exceptionOrNull() ?: IllegalArgumentException(
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
            userType = userDataStore.getUser()?.userType!!
        }
    }

    fun isParent() = userType == 2
    fun isStudent() = userType == 1

    fun uploadPhoto(
        photoType: PhotoType,
        base64Text: String,
        ext: String,
        result: (Boolean, String) -> Unit
    ) {

        val request = when (photoType) {
            PhotoType.PROFILE_PHOTO -> {
                UploadPhotoRequest(
                    type = photoType.type,
                    profile = base64Text,
                    profileExt = ext
                )
            }

            PhotoType.COVER_PHOTO -> {
                UploadPhotoRequest(
                    type = photoType.type,
                    cover = base64Text,
                    coverExt = ext
                )
            }

            PhotoType.CHILD_PHOTO -> {
                UploadPhotoRequest(
                    type = photoType.type,
                    studentPhoto = base64Text,
                    studentPhotoExt = ext
                )
            }
        }

        viewModelScope.launch {
            userRepository
                .uploadProfileIMG(request)
                .collectLatest { response ->
                    if (response.isSuccess) {
                        result(true, response.getOrNull() ?: "Success")
                    } else {
                        result(false, response.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE)
                    }
                }
        }
    }

    fun removeUser(user: NetworkUserDetailsDto) {
        viewModelScope.launch(Dispatchers.IO) {
            userDatabase.deleteUser(user.asUserEntity())
        }
    }
}

sealed interface ProfileUiState {
    object Loading : ProfileUiState

    data class Success(
        val profile: Profile,
        val users: List<NetworkUserDetailsDto>,
        val currentUserId: Int
    ) : ProfileUiState

    data class Error(
        val error: Throwable
    ) : ProfileUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}