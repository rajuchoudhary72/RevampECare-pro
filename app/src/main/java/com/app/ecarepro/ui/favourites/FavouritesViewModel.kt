package com.app.ecarepro.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    val uiState = MutableStateFlow<FavouritesUiState>(FavouritesUiState.Loading)
    private val updatedItems = mutableListOf<Favourites>()

    init {
        getFavourites()
    }

    private fun getFavourites() {
        viewModelScope.launch {
            appRepository
                .getFavourites()
                .collectLatest { result ->
                    if (result.isSuccess) {
                        uiState.value = FavouritesUiState.Success(result.getOrNull() ?: emptyList())
                    } else {
                        uiState.value = FavouritesUiState.Error(result.exceptionOrNull()!!)
                    }
                }
        }
    }

    fun onFavouriteClicked(item: Favourites) {
        viewModelScope.launch {
            if (updatedItems.any { it.menuID == item.menuID && it.chMenuID == item.chMenuID && it.sbChMenuID == item.sbChMenuID }) {
                updatedItems.removeAt(updatedItems.indexOfFirst { it.menuID == item.menuID && it.chMenuID == item.chMenuID && it.sbChMenuID == item.sbChMenuID })
            }
            updatedItems.add(item.copy(isSelected = item.isSelected?.not()))
            uiState.update { current ->
                if (current is FavouritesUiState.Success) {
                    FavouritesUiState.Success(
                        current.favourites.map {
                            if (it.menuID == item.menuID && it.chMenuID == item.chMenuID && it.sbChMenuID == item.sbChMenuID) {
                                it.copy(isSelected = it.isSelected?.not())
                            } else {
                                it
                            }
                        }
                    )
                } else {
                    current
                }
            }
        }
    }

    fun saveFavourites(func: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            appRepository
                .updateFavourites(updatedItems.map {
                    it.copy(isModified = true)
                })
                .collectLatest { result ->
                    if (result.isSuccess) {
                        func(true, "Updated")
                    } else {
                        func(false, "Failed")
                    }

                }
        }
    }
}

sealed interface FavouritesUiState {
    object Loading : FavouritesUiState

    data class Error(val error: Throwable) : FavouritesUiState

    data class Success(
        val favourites: List<Favourites>
    ) : FavouritesUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}