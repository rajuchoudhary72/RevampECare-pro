package com.app.ecarepro.feature.notice.notification_list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.NotificationItem
import com.app.ecarepro.core.domain.repository.NotificationRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationRepository,
) : BaseViewModel<NotificationsIntent, NotificationsEvent>() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    private var currentPage = 1
    private var canLoadMore = true

    init {
        loadNotifications()
    }

    override fun handleIntent(intent: NotificationsIntent) {
        when (intent) {
            is NotificationsIntent.OnBackClicked -> sendEvent(NotificationsEvent.NavigateBack)
            is NotificationsIntent.OnNotificationTapped -> viewNotification(intent.notification)
            is NotificationsIntent.LoadMore -> loadMore()
            is NotificationsIntent.Retry -> loadNotifications()
        }
    }

    private fun loadNotifications() {
        currentPage = 1
        canLoadMore = true
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getNotifications(page = 1).collect { result ->
                result.fold(
                    onSuccess = { items ->
                        val presentations = items.map { NotificationCardPresentation.from(it) }
                        canLoadMore = presentations.isNotEmpty()
                        _uiState.update {
                            it.copy(isLoading = false, notifications = presentations)
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message) }
                    },
                )
            }
        }
    }

    private fun loadMore() {
        if (!canLoadMore || _uiState.value.isLoadingMore) return
        val nextPage = currentPage + 1
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            repository.getNotifications(page = nextPage).collect { result ->
                result.fold(
                    onSuccess = { items ->
                        val newPresentations = items.map { NotificationCardPresentation.from(it) }
                        if (newPresentations.isEmpty()) {
                            canLoadMore = false
                        } else {
                            currentPage = nextPage
                        }
                        _uiState.update {
                            it.copy(
                                isLoadingMore = false,
                                notifications = it.notifications + newPresentations,
                            )
                        }
                    },
                    onFailure = {
                        _uiState.update { it.copy(isLoadingMore = false) }
                    },
                )
            }
        }
    }

    private fun viewNotification(notification: NotificationCardPresentation) {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map {
                    if (it.id == notification.id) it.copy(isRead = true) else it
                }
            )
        }
        viewModelScope.launch {
            runCatching { repository.markNotificationSeen(notification.id).collect {} }
        }
        sendEvent(
            NotificationsEvent.NavigateToDestination(
                moduleID = notification.moduleID,
                chMenuID = notification.chMenuID,
                refID = notification.refID,
            )
        )
    }
}

@Immutable
data class NotificationsUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val notifications: List<NotificationCardPresentation> = emptyList(),
)

@Immutable
data class NotificationCardPresentation(
    val id: String,
    val title: String,
    val body: String,
    val sentOn: String,
    val iconUrl: String?,
    val isRead: Boolean,
    val moduleID: Int,
    val chMenuID: Int,
    val refID: String?,
) {
    companion object {
        fun from(item: NotificationItem) = NotificationCardPresentation(
            id = item.id,
            title = item.title,
            body = item.body,
            sentOn = item.sentOn,
            iconUrl = item.icon,
            isRead = item.hasSeen,
            moduleID = item.moduleID,
            chMenuID = item.chMenuID,
            refID = item.refID,
        )
    }
}

sealed interface NotificationsIntent {
    data object OnBackClicked : NotificationsIntent
    data class OnNotificationTapped(val notification: NotificationCardPresentation) : NotificationsIntent
    data object LoadMore : NotificationsIntent
    data object Retry : NotificationsIntent
}

sealed interface NotificationsEvent {
    data object NavigateBack : NotificationsEvent
    data class NavigateToDestination(
        val moduleID: Int,
        val chMenuID: Int,
        val refID: String?,
    ) : NotificationsEvent
}
