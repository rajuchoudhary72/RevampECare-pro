package com.app.ecarepro.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Profile
import com.app.ecarepro.data.network.model.UploadPhotoRequest
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
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