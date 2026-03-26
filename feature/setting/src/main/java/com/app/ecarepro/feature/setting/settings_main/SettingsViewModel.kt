package com.app.ecarepro.feature.setting.settings_main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.domain.repository.SettingsRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.setting.navigation.SettingsNavGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val userRepository: UserRepository,
) : BaseViewModel<SettingsIntent, SettingsEvent>() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val lastSync = repository.getLastSyncDate()
            _uiState.update { it.copy(lastSyncDate = lastSync) }
        }
    }

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.OnItemClicked -> handleItemClick(intent.item)
        }
    }

    private fun handleItemClick(item: SettingsItemType) {
        when (item) {
            SettingsItemType.RATE_APP -> sendEvent(SettingsEvent.OpenPlayStore)
            SettingsItemType.CHANGE_USERNAME -> sendEvent(SettingsEvent.NavigateTo(SettingsNavGraph.ChangeUsername))
            SettingsItemType.CHANGE_PASSWORD -> sendEvent(SettingsEvent.NavigateTo(SettingsNavGraph.ChangePassword))
            SettingsItemType.CHANGE_LANGUAGE -> sendEvent(SettingsEvent.NavigateTo(SettingsNavGraph.ChangeLanguage))
            SettingsItemType.SYNC_DATA -> syncData()
        }
    }

    private fun syncData() {
        if (_uiState.value.isSyncing) return
        _uiState.update { it.copy(isSyncing = true) }
        viewModelScope.launch {
            repository.syncData().collect { result ->
                result.onSuccess { syncResult ->
                    _uiState.update { it.copy(isSyncing = false, lastSyncDate = syncResult.syncTimestamp) }
                    sendEvent(SettingsEvent.ShowMessage(SnackbarMessage("Data synced successfully", MessageType.SUCCESS)))
                }
                result.onFailure { error ->
                    _uiState.update { it.copy(isSyncing = false) }
                    sendEvent(SettingsEvent.ShowMessage(SnackbarMessage(error.message ?: "Sync failed", MessageType.ERROR)))
                }
            }
        }
    }
}

@Immutable
data class SettingsUiState(
    val isSyncing: Boolean = false,
    val lastSyncDate: String? = null,
)

sealed interface SettingsIntent {
    data class OnItemClicked(val item: SettingsItemType) : SettingsIntent
}

sealed interface SettingsEvent {
    data class NavigateTo(val destination: NavKey) : SettingsEvent
    data object OpenPlayStore : SettingsEvent
    data class ShowMessage(val message: SnackbarMessage) : SettingsEvent
}

enum class SettingsItemType {
    RATE_APP,
    CHANGE_USERNAME,
    CHANGE_PASSWORD,
    CHANGE_LANGUAGE,
    SYNC_DATA,
}
