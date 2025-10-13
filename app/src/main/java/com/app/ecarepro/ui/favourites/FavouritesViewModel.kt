package com.app.ecarepro.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.FavouritesUpdateDto
import com.app.ecarepro.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val analyticsManager: AnalyticsManager
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
                        uiState.value = FavouritesUiState.Success(
                            result.getOrNull()?.sortedBy { it.isSelected == false } ?: emptyList())
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
            if (uiState.value is FavouritesUiState.Success) {
                val maxSl: Int =
                    (uiState.value as FavouritesUiState.Success).favourites.maxByOrNull {
                        it.slNo ?: 0
                    }?.slNo ?: 0
                val favourites: List<Favourites> = updatedItems.mapIndexed { index, favourites ->
                    favourites.copy(isModified = true, slNo = maxSl.plus(index + 1))
                }
                appRepository
                    .updateFavourites(updatedItems.mapIndexed { index, favourites ->
                        favourites.copy(isModified = true, slNo = maxSl.plus(index + 1))
                    })
                    .collectLatest { result ->
                        if (result.isSuccess) {
                            func(true, "Updated")
                            sendAnalyticEvent(
                                AnalyticsConstants.Events.UPDATE_FAVOURITES,
                                mapOf(
                                    AnalyticsConstants.Attributes.FAVOURITES to favourites.toString()
                                )
                            )
                        } else {
                            func(false, "Failed")
                        }
                    }
            }
        }
    }
    fun sendScreenEvent() {
        analyticsManager.trackScreen(
            AnalyticsConstants.Screens.FAVOURITES
        )
    }
    fun sendAnalyticEvent(
        event: String,
        attributes: Map<String, String>
    ) {
        analyticsManager.trackEvent(
            event,
            attributes
        )
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