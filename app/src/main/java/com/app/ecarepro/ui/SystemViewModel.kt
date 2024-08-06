package com.app.ecarepro.ui

import android.app.Application.WIFI_SERVICE
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings.Secure
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Menu
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.network.model.SearchOption
import com.app.ecarepro.data.network.model.UserInfo
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SystemViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore,
    private val appRepository: AppRepository,
    private val userRepository: UserRepository,
    private val schoolRepository: SchoolRepository

) : ViewModel() {
    private val _openNavigationDrawer = MutableLiveData(false)
    val openNavigationDrawer = _openNavigationDrawer

    private val _navigateBack = MutableSharedFlow<Boolean>()
    val navigateBack = _navigateBack

    val refresh = MutableSharedFlow<Boolean>()
    val user = userDataStore.getUserAsFlow()
    var userRoleName: String = ""

    init {
        viewModelScope.launch {
            userRoleName = userDataStore.getRoleName().toString()
        }
    }


    val uiState =
        refresh.flatMapLatest {
            appRepository.getAppLayout()
        }
            .map { result ->
                if (result.isSuccess) {
                    val response = result.getOrNull()!!
                    MainActivityUiState.Success(
                        userInfo = response.userInfo,
                        menus = response.menus ?: emptyList(),
                        favroiteMenus = response.favoriteMenus ?: emptyList(),
                        searchOption = response.searchOptions ?: emptyList()
                    )
                } else {
                    val error = result.exceptionOrNull() ?: IllegalArgumentException(
                        UNKNOWN_ERROR_MESSAGE
                    )
                    MainActivityUiState.Error(
                        error
                    )
                }
            }
            .stateIn(
                initialValue = MainActivityUiState.Loading,
                started = SharingStarted.WhileSubscribed(300),
                scope = viewModelScope
            )

    fun getSearchOptions(): List<SearchOption> {
        val uiState = uiState.value
        return if (uiState is MainActivityUiState.Success) {
            uiState.searchOption
        } else {
            emptyList()
        }
    }

    fun openDrawer(open: Boolean) {
        _openNavigationDrawer.postValue(open)
    }

    fun navigateBack(back: Boolean) {
        viewModelScope.launch {
            _navigateBack.emit(back)
        }
    }


    fun logout(onDataClear: (Boolean) -> Unit) {
        viewModelScope.launch {
            userRepository.logout().collectLatest { result ->
                if (result.isSuccess) {
                    try {
                        userDataStore.clear()
                        onDataClear(true)
                    } catch (e: Exception) {
                        e.toString()
                        onDataClear(false)
                    }
                }
            }
        }
    }

    fun refreshAppLayout() {
        viewModelScope.launch {
            refresh.emit(true)
        }
    }

    fun registerDeviceToken(token: String) {
        GlobalScope.launch {
            val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager
            val wInfo = wifiManager.connectionInfo
            val macAddress = wInfo.macAddress
            appRepository
                .registerDevice(
                    RegisterDevice(
                        fcmToken = token,
                        osVersion = "OS " + Build.VERSION.SDK_INT,
                        deviceModel = Build.MANUFACTURER + " " + Build.MODEL,
                        deviceType = 1,
                        imeI1 = macAddress,
                        imeI2 = macAddress,
                        deviceID = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
                    )
                )
                .collectLatest {
                    println(it)
                }
        }

    }

    fun getTokenKey(function: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                function(
                    userRepository
                        .getGenerateToken(Constant.DEVICE_TYPE).tokenKey
                )
            } catch (e: Exception) {
                function(null)
            }
        }
    }

    fun fetchSettings(){
        viewModelScope.launch {
            schoolRepository.getGeneralSettings().collectLatest {
                it.onSuccess {
                    userDataStore.saveGeneralSettings(it)
                }
            }
        }
    }
}

sealed interface MainActivityUiState {
    object Loading : MainActivityUiState

    data class Success(
        val userInfo: UserInfo,
        val menus: List<Menu>,
        val favroiteMenus: List<Menu>,
        val searchOption: List<SearchOption> = emptyList(),
    ) : MainActivityUiState

    data class Error(
        val error: Throwable,
    ) : MainActivityUiState

    fun isLoading() = this == Loading
    fun getErrorOrNull() = if (this is Error) this.error else null
    fun getValueOrNull() = if (this is Success) this else null
}