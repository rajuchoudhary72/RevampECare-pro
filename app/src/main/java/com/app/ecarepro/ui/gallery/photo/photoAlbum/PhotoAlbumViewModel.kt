package com.app.ecarepro.ui.gallery.photo.photoAlbum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAlbumType
import com.app.ecarepro.data.network.model.NetworkPhotoAlbum
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoAlbumViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val photoAlbumMutableStateFlow: MutableStateFlow<NetworkResult<NetworkPhotoAlbum>> = MutableStateFlow(
        NetworkResult.Loading())
    val  photoAlbumStateFlow: StateFlow<NetworkResult<NetworkPhotoAlbum>> = photoAlbumMutableStateFlow



    fun getPhotoAlbums(
        typeID: Int,
        pg: Int,
    )=viewModelScope.launch {
        runCatching {
            photoAlbumMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.getPhotoAlbums( typeID, pg)
        }.onSuccess {
            photoAlbumMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            photoAlbumMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}