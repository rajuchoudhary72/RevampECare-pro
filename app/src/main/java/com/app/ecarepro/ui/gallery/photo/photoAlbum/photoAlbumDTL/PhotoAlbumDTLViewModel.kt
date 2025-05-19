package com.app.ecarepro.ui.gallery.photo.photoAlbum.photoAlbumDTL

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.Photo
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoAlbumDTLViewModel @Inject constructor(
    private val userRepository: UserRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val id: String = savedStateHandle[Constant.ID] ?: throw IllegalArgumentException()

    private val _photoAlbumStateFlow: MutableLiveData<NetworkResult<NetworkAlbumPhotoDetails>> =
        MutableLiveData(NetworkResult.Loading())

    val photoAlbumStateFlow: LiveData<NetworkResult<NetworkAlbumPhotoDetails>> =
        _photoAlbumStateFlow

    var cachedData: NetworkAlbumPhotoDetails? = null
    var cachedPhotoList: ArrayList<Photo> = ArrayList()
    private var lastLoadedAlbumId: String? = null
    var lastPageIndex: Int? = 0

    init {
        getPhotoAlbumDTL(id, 1)
    }

    fun getPhotoAlbumDTL(iD: String, pg: Int) = viewModelScope.launch {
        runCatching {
            _photoAlbumStateFlow.value = NetworkResult.Loading()
            userRepository.getPhotoAlbumDTL(iD, pg)
        }.onSuccess {
            _photoAlbumStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            _photoAlbumStateFlow.value = NetworkResult.Error(it.message)
        }
    }
}
