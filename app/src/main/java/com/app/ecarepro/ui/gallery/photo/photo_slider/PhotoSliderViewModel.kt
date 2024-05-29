package com.app.ecarepro.ui.gallery.photo.photo_slider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkAlbumType
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoSliderViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val manageFavoritesMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val  manageFavoritesTypesStateFlow: StateFlow<NetworkResult<CommonResponse>> = manageFavoritesMutableStateFlow



    fun manageFavorites(
        id: String,
        galleryType: Int,
        action: String
    )=viewModelScope.launch {
        runCatching {
            manageFavoritesMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.manageFavorites( id, galleryType, action)
        }.onSuccess {
            manageFavoritesMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            manageFavoritesMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

    fun manageLikes(
        id: String,
        galleryType: Int,
        like: Boolean
    )=viewModelScope.launch {
        runCatching {
            manageFavoritesMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.manageLikes( id, galleryType, like)
        }.onSuccess {
            manageFavoritesMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            manageFavoritesMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}