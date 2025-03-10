package com.app.ecarepro.ui.gallery.photo.photoAlbum.photoAlbumDTL

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
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
class PhotoAlbumDTLViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val photoAlbumMutableStateFlow: MutableLiveData<NetworkResult<NetworkAlbumPhotoDetails>> = MutableLiveData(
        NetworkResult.Loading())
    val  photoAlbumStateFlow: LiveData<NetworkResult<NetworkAlbumPhotoDetails>> = photoAlbumMutableStateFlow



    fun getPhotoAlbumDTL(
        iD: String,
        pg: Int,
    )=viewModelScope.launch {
        runCatching {
            photoAlbumMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.getPhotoAlbumDTL( iD, pg)
        }.onSuccess {
            photoAlbumMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            photoAlbumMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}