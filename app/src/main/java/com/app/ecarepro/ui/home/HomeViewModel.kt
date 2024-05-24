package com.app.ecarepro.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Card
import com.app.ecarepro.data.network.model.Menu
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.Slider
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val userRepository: UserRepository
) : ViewModel() {

    val schoolData = MutableLiveData<NetworkSchool>()

    private val favouriteData = MutableStateFlow<List<Menu>?>(null)

    val uiState =

        combine(
            flow = userRepository.getUserDashboard(),
            flow2 = userRepository.getUserUndertaking(),
            flow3 = favouriteData
        ) { dashboard, undertaking, favourite ->
            Triple(dashboard, undertaking, favourite)
        }

            .map { (dashboard, undertaking, favourite) ->
                if (dashboard.isSuccess && undertaking.isSuccess) {
                    val response = dashboard.getOrNull()
                    val cards = mutableListOf<Card>()

                    if (response?.showProCards == true) {
                        cards.addAll(response.proCards ?: emptyList())
                    }

                    if (response?.showCards == true) {
                        cards.addAll(response.cards ?: emptyList())
                    }

                    HomeUiState.Success(
                        cards = cards,
                        favourites = favourite?.map {
                            Slider(
                                imgPath = it.icon,
                                module = it.title ?: ""
                            )
                        } ?: emptyList(),
                        user = userDataStore.getUser()!!,
                        underTaking = undertaking.getOrNull() ?: ""
                    )
                } else {
                    HomeUiState.Error(
                        dashboard.exceptionOrNull() ?: IllegalStateException(
                            UNKNOWN_ERROR_MESSAGE
                        )
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = HomeUiState.Loading,
                started = SharingStarted.WhileSubscribed(500)
            )

    fun submitUserUndertaking(id: String, function: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            userRepository.saveUserUndertaking(id).collectLatest {
                function(
                    it.isSuccess,
                    it.getOrNull() ?: it.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                )
            }
        }
    }

    fun setFavourite(menu: List<Menu>) {
        favouriteData.update { menu }
    }


    init {
        viewModelScope.launch {
            schoolData.postValue(userDataStore.getSchoolData())
        }
    }
}

