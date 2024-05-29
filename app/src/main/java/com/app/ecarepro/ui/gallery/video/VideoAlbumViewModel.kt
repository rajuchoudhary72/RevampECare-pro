package com.app.ecarepro.ui.gallery.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAlbumType
import com.app.ecarepro.data.network.model.NetworkPhotoAlbum
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkVideoAlbum
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoAlbumViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val photoAlbumMutableStateFlow: MutableStateFlow<NetworkResult<NetworkVideoAlbum>> = MutableStateFlow(
        NetworkResult.Loading())
    val  photoAlbumStateFlow: StateFlow<NetworkResult<NetworkVideoAlbum>> = photoAlbumMutableStateFlow



    fun getVideoAlbums(
        pg: Int,
    )=viewModelScope.launch {
        runCatching {
            photoAlbumMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.getVideoAlbums(  pg)
        }.onSuccess {
            photoAlbumMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            photoAlbumMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}