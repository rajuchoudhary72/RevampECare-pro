package com.app.ecarepro.data.sync

import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.asUserEntity
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SyncManager @Inject constructor(
    private val appRepository: AppRepository,
    private val userDataStore: UserDataStore,
    private val userDatabase: UserDatabase
) {

    suspend fun sync(
        forceSync: Boolean = true,
        resultListener: ((success: Boolean, message: String) -> Unit)? = null
    ) {
        val userToUpdate = userDataStore.getUser()
        when {
            userDataStore.isUserAuthenticated().not() -> {
                resultListener?.invoke(false, "User not authenticated")
            }

            forceSync -> {
                startSyncing(userToUpdate, resultListener)
            }

            isLastSyncMoreThan24HoursFromNow(userToUpdate?.loginTime) -> {
                startSyncing(userToUpdate, resultListener)
            }

            else -> {
                resultListener?.invoke(false, "No need to sync")
            }
        }
    }

    private suspend fun startSyncing(
        userToUpdate: NetworkUserDetailsDto?,
        resultListener: ((success: Boolean, message: String) -> Unit)?
    ) {
        appRepository.syncData().collect { result ->
            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null) {
                    updateDataToDatabase(data, userToUpdate)
                    resultListener?.invoke(true, "Sync successful")
                } else {
                    resultListener?.invoke(
                        false,
                        result.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                    )
                }
            } else {
                resultListener?.invoke(
                    false,
                    result.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                )
            }
        }
    }

    private suspend fun updateDataToDatabase(
        data: LoginResponseDto,
        userToUpdate: NetworkUserDetailsDto?
    ) {
        val user = data.asUserEntity()
        userToUpdate?.let { currentUserInDatabase ->
            userDatabase.deleteUserById(currentUserInDatabase.id)
            val id = userDatabase.insertUser(
                user.copy(
                    schoolCode = userDataStore.getCurrentSchoolCode(),
                    loginTime = getCurrentSyncTime()
                )
            )
            userDataStore.setCurrentUserId(id.toInt())
        }

    }

    private fun isLastSyncMoreThan24HoursFromNow(dateString: String?): Boolean {
        if (dateString.isNullOrEmpty()) return true

        val dateFormat = SimpleDateFormat(SYNC_TIME_FORMAT, Locale.getDefault())

        return try {
            val inputDate = dateFormat.parse(dateString) ?: return false
            val currentDate = Calendar.getInstance().time

            val differenceInMillis = inputDate.time - currentDate.time
            differenceInMillis > 24 * 60 * 60 * 1000 // 24 hours in milliseconds
        } catch (e: Exception) {
            false // Handle parsing errors
        }
    }

    private fun getCurrentSyncTime(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat(SYNC_TIME_FORMAT, Locale.getDefault())
        return dateFormat.format(currentDate)
    }


    companion object {
        private const val SYNC_TIME_FORMAT = "dd/MM/yyyy hh:mm a"
    }
}