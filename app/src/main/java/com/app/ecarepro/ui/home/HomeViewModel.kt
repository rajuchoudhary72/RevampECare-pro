package com.app.ecarepro.ui.home

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Card
import com.app.ecarepro.data.network.model.DashboardButtons
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
import com.app.ecarepro.data.network.model.Menu
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.network.model.Slider
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.data.network.model.UserUndertakingModule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore,
    private val userRepository: UserRepository
) : ViewModel() {

    val schoolData = MutableLiveData<NetworkSchool>()
    val dashboardButtons = MutableLiveData<List<DashboardButtons>?>()
    var currentLocation: Pair<Double, Double>? = null
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
                    dashboardButtons.value = dashboard.getOrNull()?.dashboardButtons
                    val cards = mutableListOf<Card>()

                    if (response?.showProCards == true) {
                        cards.addAll(response.proCards ?: emptyList())
                    }

                    if (response?.showCards == true) {
                        cards.addAll(response.cards ?: emptyList())
                    }

                    HomeUiState.Success(
                        cards = cards,
                        favourites = favourite ?: emptyList(),
                        user = userDataStore.getUser()!!,
                        underTaking = undertaking.getOrNull() ?: ""
                    )
                } else {
                    try {
                        HomeUiState.Error(
                            dashboard.exceptionOrNull() ?: IllegalStateException(
                                UNKNOWN_ERROR_MESSAGE
                            )
                        )
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }

                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = HomeUiState.Loading,
                started = SharingStarted.WhileSubscribed(500)
            )

    fun submitUserUndertaking(id: String, function: (Boolean, String) -> Unit) {
        viewModelScope.launch {

            userRepository
                .saveUserUndertaking(
                    UserUndertakingModule(
                        UtID = id,
                        deviceID = Settings.Secure.getString(
                            context.contentResolver,
                            Settings.Secure.ANDROID_ID
                        ),
                        deviceModel = Build.MANUFACTURER + " " + Build.MODEL,
                        deviceType = 1,
                        geoCoordinate = currentLocation.toString().replace("(", "")
                            .replace(")", "")
                    )
                )
                .collectLatest {
                    try {
                        function(
                            it.isSuccess,
                            it.getOrNull() ?: it.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                        )
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }

                }
            /*  userRepository.saveUserUndertaking(id,
                deviceModel = Build.MANUFACTURER + " " + Build.MODEL,
                deviceType = 1,).collectLatest {
                function(
                    it.isSuccess,
                    it.getOrNull() ?: it.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                )
            }*/
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



