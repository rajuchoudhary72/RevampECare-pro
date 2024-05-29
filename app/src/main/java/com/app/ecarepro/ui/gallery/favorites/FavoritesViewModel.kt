package com.app.ecarepro.ui.gallery.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkAlbumType
import com.app.ecarepro.data.network.model.NetworkFavorites
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
class FavoritesViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val favMutableStateFlow: MutableStateFlow<NetworkResult<NetworkFavorites>> = MutableStateFlow(
        NetworkResult.Loading())
    val  favStateFlow: StateFlow<NetworkResult<NetworkFavorites>> = favMutableStateFlow



    fun getFavorites(

        pg: Int,
    )=viewModelScope.launch {
        runCatching {
            favMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.getFavorites(  pg)
        }.onSuccess {
            favMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            favMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}