package com.app.ecarepro.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Card
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val userRepository: UserRepository
) : ViewModel() {

    val schoolData = MutableLiveData<NetworkSchool>()

    val uiState =

        combine(
            flow = userRepository.getUserDashboard(),
            flow2 = userRepository.getUserUndertaking()
        ) { dashboard, undertaking ->
            Pair(dashboard, undertaking)
        }

            .map { (dashboard, undertaking) ->
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
                        favourites = userDataStore.getSchoolData()?.slider ?: emptyList(),
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
                function(it.isSuccess, it.getOrNull()?:it.exceptionOrNull()?.message?: UNKNOWN_ERROR_MESSAGE)
            }
        }
    }


    init {
        viewModelScope.launch {
            schoolData.postValue(userDataStore.getSchoolData())
        }
    }
}

