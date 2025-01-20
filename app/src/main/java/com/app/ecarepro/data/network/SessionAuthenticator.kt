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
import android.util.Log
import com.app.ecarepro.data.network.AuthInterceptor.Companion.AUTH_TOKEN
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.withLock


class SessionAuthenticator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore,
    @SessionReCreate private val userService: UserService
) : Authenticator {

    private val mutex = Mutex()

    @SuppressLint("HardwareIds")
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            mutex.withLock {
                try {
                    // Refresh the session and save the new session ID
                    val cachedSessionID = userDataStore.getUserSessionId()

                    // Check if the session was already refreshed
                    if (response.request.header(SESSION_ID) != cachedSessionID) {

                        return@runBlocking cachedSessionID?.let {
                            Log.e("API DATA OLD", "API URL ("+response.request.url.toString()+") \n AUTH TOKEN ("+response.header(SESSION_ID)+") \n SESSION ID ("+response.header(AUTH_TOKEN)+")")
                            response.request.newBuilder()
                                .header(SESSION_ID, it)
                                .build()
                        }
                    } else {
                        val newSessionID = refreshSession()
                        newSessionID?.let {
                            userDataStore.saveSessionId(it) // Save the new session ID
                            Log.e("API DATA NEW", "API URL ("+response.request.url.toString()+") \n AUTH TOKEN ("+response.header(SESSION_ID)+") \n SESSION ID ("+it+")")
                            response.request.newBuilder()
                                .header(SESSION_ID, it)
                                .build()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppSessionManager.logoutAndRestartApp(true) // Handle failure gracefully
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