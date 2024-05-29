package com.app.ecarepro.ui.gallery.mediaGallery

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaGalleryViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val mediaGalleryMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMediaGallery>> = MutableStateFlow(
        NetworkResult.Loading())
    val  mediaGalleryStateFlow: StateFlow<NetworkResult<NetworkMediaGallery>> = mediaGalleryMutableStateFlow



    fun getMediaGallery(
        pg: Int,
        queryType: Int,
        year: Int,
        date: String,
        query: String
    )=viewModelScope.launch {
        runCatching {
            mediaGalleryMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.getMediaGallery(  pg, queryType, year, date, query)
        }.onSuccess {
            mediaGalleryMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            mediaGalleryMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}