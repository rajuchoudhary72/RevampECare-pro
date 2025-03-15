package com.app.ecarepro.ui.gallery.kid_corner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkAlbumType
import com.app.ecarepro.data.network.model.NetworkFavorites
import com.app.ecarepro.data.network.model.NetworkMediaGallery
import com.app.ecarepro.data.network.model.NetworkPhotoAlbum
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkVideoAlbumDTL
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.NetworkKidCornerModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KidCornerViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val mediaGalleryMutableStateFlow: MutableStateFlow<NetworkResult<NetworkKidCornerModel>> = MutableStateFlow(
        NetworkResult.Loading())
    val  mediaGalleryStateFlow: StateFlow<NetworkResult<NetworkKidCornerModel>> = mediaGalleryMutableStateFlow



    fun getKidsCornerAlbums(
        pg: Int,
    )=viewModelScope.launch {
        runCatching {
            mediaGalleryMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.getKidsCornerAlbums(  pg)
        }.onSuccess {
            mediaGalleryMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            mediaGalleryMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

    fun getSearchKidsAlbum(
        pg: Int,
        yrID: Int,
        keyword: String?
    )=viewModelScope.launch {
        runCatching {
            mediaGalleryMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.getSearchKidsAlbum(  pg,yrID,keyword)
        }.onSuccess {
            mediaGalleryMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            mediaGalleryMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}