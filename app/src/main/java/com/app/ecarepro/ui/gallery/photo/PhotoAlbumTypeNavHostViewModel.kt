package com.app.ecarepro.ui.gallery.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAlbumType
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoAlbumTypeNavHostViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val photoAlbumTypesMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAlbumType>> = MutableStateFlow(
        NetworkResult.Loading())
    val  photoAlbumTypesStateFlow: StateFlow<NetworkResult<NetworkAlbumType>> = photoAlbumTypesMutableStateFlow



    fun getPhotoAlbumTypes(  )=viewModelScope.launch {
        runCatching {
            photoAlbumTypesMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.getPhotoAlbumTypes( )
        }.onSuccess {
            photoAlbumTypesMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            photoAlbumTypesMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}