package com.app.ecarepro.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Menu
import com.app.ecarepro.data.network.model.UserInfo
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SystemViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val appRepository: AppRepository
) : ViewModel() {

    private val _openNavigationDrawer = MutableLiveData(false)
    val openNavigationDrawer = _openNavigationDrawer

    private val _navigateBack = MutableSharedFlow<Boolean>()
    val navigateBack = _navigateBack

    val refresh = MutableStateFlow(false)

    val uiState =
        refresh.flatMapLatest {
            appRepository.getAppLayout()
        }
            .map { result ->
                if (result.isSuccess) {
                    val response = result.getOrNull()!!
                    MainActivityUiState.Success(
                        userInfo = response.userInfo,
                        menus = response.menus ?: emptyList()
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

    fun openDrawer(open: Boolean) {
        _openNavigationDrawer.postValue(open)
    }

    fun navigateBack(back: Boolean) {
        viewModelScope.launch {
            _navigateBack.emit(back)
        }
    }

    fun logout(onDataClear: () -> Unit) {
        viewModelScope.launch {
            userDataStore.clear()
            onDataClear()
        }
    }
}

sealed interface MainActivityUiState {
    object Loading : MainActivityUiState

    data class Success(
        val userInfo: UserInfo,
        val menus: List<Menu>
    ) : MainActivityUiState

    data class Error(
        val error: Throwable,
    ) : MainActivityUiState

    fun isLoading() = this == Loading
    fun getErrorOrNull() = if (this is Error) this.error else null
    fun getValueOrNull() = if (this is Success) this else null
}