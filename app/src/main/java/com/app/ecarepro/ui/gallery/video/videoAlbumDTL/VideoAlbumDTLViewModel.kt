package com.app.ecarepro.ui.gallery.video.videoAlbumDTL

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkAlbumType
import com.app.ecarepro.data.network.model.NetworkPhotoAlbum
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkVideoAlbumDTL
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoAlbumDTLViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val photoAlbumMutableStateFlow: MutableStateFlow<NetworkResult<NetworkVideoAlbumDTL>> = MutableStateFlow(
        NetworkResult.Loading())
    val  photoAlbumStateFlow: StateFlow<NetworkResult<NetworkVideoAlbumDTL>> = photoAlbumMutableStateFlow



    fun getVideoAlbumDTL(
        id: String,
        pg: Int,
    )=viewModelScope.launch {
        runCatching {
            photoAlbumMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.getVideoAlbumDTL( id, pg)
        }.onSuccess {
            photoAlbumMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            photoAlbumMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}