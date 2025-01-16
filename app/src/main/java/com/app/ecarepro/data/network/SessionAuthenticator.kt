package com.app.ecarepro.data.network

import android.content.Context
import android.provider.Settings.Secure
import com.app.ecarepro.data.AppSessionManager
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.AuthInterceptor.Companion.SESSION_ID
import com.app.ecarepro.data.network.service.UserService
import com.app.ecarepro.di.annotations.SessionReCreate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import android.annotation.SuppressLint
import java.util.concurrent.atomic.AtomicBoolean

class SessionAuthenticator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore,
    @SessionReCreate private val userService: UserService
) : Authenticator {

    @Volatile
    private var isRefreshing = AtomicBoolean(false) // Track session refresh status
    private var newSessionID: String? = null // Cache the new session ID

    @SuppressLint("HardwareIds")
    override fun authenticate(route: Route?, response: Response): Request? {
        return synchronized(this) {
            if (isRefreshing.compareAndSet(false, true)) {
                // First thread to enter the block will refresh the session
                try {
                    runBlocking {
                        newSessionID = refreshSession() // Attempt to refresh the session
                        newSessionID?.let {
                            userDataStore.saveSessionId(it) // Save the new session ID
                        }
                    }
                } finally {
                    isRefreshing.set(false) // Reset the flag after refreshing
                }
            } else {
                // Wait for the ongoing refresh to complete
                while (isRefreshing.get()) {
                    Thread.sleep(50) // Short sleep to prevent busy waiting
                }
            }

            // Use the refreshed session ID for the request
            return newSessionID?.let {
                response.request.newBuilder()
                    .header(SESSION_ID, it)
                    .build()
            }
        }
    }

    /**
     * Refresh the session by calling the API and retrieving a new session ID.
     * @return New session ID or null if the refresh fails.
     */
    private suspend fun refreshSession(): String? {
        return try {
            val requestDto = CreateUserSessionRequestDto(
                ipAddress = Secure.getString(context.contentResolver, Secure.ANDROID_ID),
                locationCity = userDataStore.getCityName(),
                oldSessionID = userDataStore.getUserSessionId()
            )
            userService.createSession(requestDto).sessionID
        } catch (e: Exception) {
            e.printStackTrace() // Log the error for debugging
            null
        }
    }
}
