package com.app.ecarepro.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.Notification
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    val appRepository: AppRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {

    val uiState = appRepository
        .getNotifications()
        .map { result ->
            if (result.isSuccess) {
                val notifications = result.getOrNull() ?: emptyList()
                NotificationUiState.Success(notifications)
            } else {
                NotificationUiState.Error(
                    result.exceptionOrNull() ?: IllegalArgumentException(
                        UNKNOWN_ERROR_MESSAGE
                    )
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = NotificationUiState.Loading,
            started = SharingStarted.WhileSubscribed(400)
        )

    fun markNotificationAsSeen(id: String) {
        viewModelScope.launch {
            appRepository.notificationSeen(id)
        }
    }

    fun sendScreenEvent(){
        analyticsManager.trackScreen(AnalyticsConstants.Screens.NOTIFICATION_LIST)
    }
}

sealed interface NotificationUiState {

    object Loading : NotificationUiState

    data class Success(
        val notifications: List<Notification>
    ) : NotificationUiState

    data class Error(
        val error: Throwable
    ) : NotificationUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}