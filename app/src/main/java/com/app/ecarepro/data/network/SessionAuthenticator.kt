package com.app.ecarepro.data.network

import android.content.Context
import android.provider.Settings.Secure
import android.util.Log
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
import com.app.ecarepro.data.network.AuthInterceptor.Companion.AUTH_TOKEN

class SessionAuthenticator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore,
    @SessionReCreate private val userService: UserService
) : Authenticator {

    @SuppressLint("HardwareIds")
    override fun authenticate(route: Route?, response: Response): Request? {
        return synchronized(this) {
            runBlocking {
                try {
                    val sessionID = if (userDataStore.shouldCallCreateSession()) {
                        userService.createSession(
                            CreateUserSessionRequestDto(
                                ipAddress = Secure.getString(
                                    context.contentResolver,
                                    Secure.ANDROID_ID
                                ),
                                locationCity = userDataStore.getCityName(),
                                oldSessionID = userDataStore.getUserSessionId()
                            )
                        ).also {
                            userDataStore.saveSessionId(it.sessionID)
                            userDataStore.saveCreateSessionTime(System.currentTimeMillis())
                        }.sessionID
                    } else {
                        userDataStore.getUserSessionId()
                    }
                    response.request.newBuilder()
                        .header(SESSION_ID, sessionID.orEmpty())
                        .build()
                } catch (e: Exception) {
                    AppSessionManager.logoutAndRestartApp(true)
                    null
                }
            }
        }
    }

    /**
     * Refresh the session by calling the API and retrieving a new session ID.
     * @return New session ID or null if the refresh fails.
     */
    private suspend fun refreshSession(): String? {
        return try {
            Log.d("SessionAuthenticator", "Attempting to refresh session...")

            val requestDto = CreateUserSessionRequestDto(
                ipAddress = Secure.getString(context.contentResolver, Secure.ANDROID_ID),
                locationCity = userDataStore.getCityName(),
                oldSessionID = userDataStore.getUserSessionId()
            )
            Log.d("SessionAuthenticator", "Sending session creation request: $requestDto")

            val sessionID = userService.createSession(requestDto).sessionID
            Log.d("SessionAuthenticator", "Received new session ID from API: $sessionID")

            sessionID
        } catch (e: Exception) {
            Log.e("SessionAuthenticator", "Error during session refresh: ${e.message}", e)
            null
        }
    }
}
