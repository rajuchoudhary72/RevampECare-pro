package com.app.ecarepro.core.data.repository

import android.content.Context
import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.SyncResult
import com.app.ecarepro.core.domain.repository.SettingsRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.network.SettingsRemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private const val PREFS_NAME = "settings_prefs"
private const val KEY_LAST_SYNC = "last_sync_date"

internal class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsRemoteDataSource,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context,
) : SettingsRepository {

    private val prefs by lazy { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun changeUsername(
        currentUsername: String,
        newUsername: String,
    ): Flow<Result<String>> = asResultFlow {
        val response = dataSource.changeUsername(currentUsername, newUsername)
        if (response.status == "ok" && response.errorCode == 0) {
            response.message
        } else {
            throw Exception(response.message)
        }
    }

    override fun changePassword(
        currentUsername: String,
        newPassword: String,
    ): Flow<Result<String>> = asResultFlow {
        val response = dataSource.changePassword(currentUsername, newPassword)
        if (response.status == "ok") {
            response.message
        } else {
            throw Exception(response.message)
        }
    }

    override fun syncData(): Flow<Result<SyncResult>> = asResultFlow {
        val timestamp = SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault())
            .format(Date())
        prefs.edit().putString(KEY_LAST_SYNC, timestamp).apply()
        SyncResult(syncTimestamp = timestamp)
    }

    override suspend fun getLastSyncDate(): String? = prefs.getString(KEY_LAST_SYNC, null)
}
