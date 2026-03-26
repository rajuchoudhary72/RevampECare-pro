package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.SyncResult
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun changeUsername(currentUsername: String, newUsername: String): Flow<Result<String>>
    fun changePassword(currentUsername: String, newPassword: String): Flow<Result<String>>
    fun syncData(): Flow<Result<SyncResult>>
    suspend fun getLastSyncDate(): String?
}
