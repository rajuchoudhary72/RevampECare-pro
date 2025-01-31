package com.app.ecarepro.data.network

import android.content.Context
import android.provider.Settings.Secure
import android.util.Log
import com.app.ecarepro.data.AppSessionManager
import com.app.ecarepro.data.datastore.UserDataStore
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
        return runBlocking {
            try {
                Log.d("SessionAuthenticator", "authenticate() called for URL: ${response.request.url}")
               /* Log.e("Raju Log ", "API URL ("+response.request.url.toString()+") \n AUTH TOKEN ("+response.request.header(AUTH_TOKEN)+") \n SESSION ID ("+response.request.header(
                    SESSION_ID)+") \n CODE ("+response.code+")")*/

                // Always create a new session
                val newSessionID = refreshSession()

                // Save the new session ID
                newSessionID?.let {
                    Log.d("SessionAuthenticator", "New session ID created: $it")
                    userDataStore.saveSessionId(it)
                    Log.d("SessionAuthenticator", "New session ID saved in UserDataStore")
                    Log.e("API DATA", "API URL ("+response.request.url.toString()+") \n AUTH TOKEN ("+response.request.header(AUTH_TOKEN)+") \n SESSION ID ("+it+")")

                    /*return@runBlocking response.request.newBuilder()
                        .header(SESSION_ID, it)
                        .build()*/
                }

                // If session creation fails, logout the user
                Log.e("SessionAuthenticator", "Failed to create new session ID. Logging out.")
                AppSessionManager.logoutAndRestartApp(true)
                return@runBlocking null

            } catch (e: Exception) {
                Log.e("SessionAuthenticator", "Error during session authentication: ${e.message}", e)
                AppSessionManager.logoutAndRestartApp(true)
                null
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
