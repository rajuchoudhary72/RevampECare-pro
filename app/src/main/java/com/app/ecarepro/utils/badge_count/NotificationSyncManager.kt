package com.app.ecarepro.utils.badge_count

import com.app.ecarepro.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class NotificationSyncManager @Inject constructor(
    private val appRepository: AppRepository
) {
    /*for notification */
    private val _badgeCountFlow = MutableStateFlow(0)
    val badgeCountFlow: StateFlow<Int> get() = _badgeCountFlow

    /*for message */
    private val _badgeCountMessageFlow = MutableStateFlow(0)
    val badgeCountMessageFlow: StateFlow<Int> get() = _badgeCountMessageFlow

    suspend fun fetchAndUpdateBadgeCount() {
        try {
            val response = appRepository.getNotificationCount()
            if (response.errorCode == 0) {
                _badgeCountFlow.value = response.notificationCount
                _badgeCountMessageFlow.value = response.messageCount
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
