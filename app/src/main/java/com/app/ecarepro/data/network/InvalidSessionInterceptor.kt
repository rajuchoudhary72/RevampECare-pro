package com.app.ecarepro.data.network

import android.util.Log
import com.app.ecarepro.data.AppSessionManager
import com.app.ecarepro.data.datastore.UserDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class InvalidSessionInterceptor @Inject constructor(
    private val userDataStore: UserDataStore
) : Interceptor {

    companion object {
        private const val AUTH_TOKEN = "AuthToken"
        private const val SESSION_ID = "SessionID"
        private const val TAG = "InvalidSessionInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val originalRequest = chain.request()
            val originalResponse = chain.proceed(originalRequest)

            if (originalResponse.code == 403) {
                return@runBlocking handle403Response(originalResponse, chain)
            }
            originalResponse
        }
    }

    private suspend fun handle403Response(response: Response, chain: Interceptor.Chain): Response {
        if (userDataStore.shouldCallCreateSession()) {
            logInvalidSessionDetails(response)
            AppSessionManager.logoutAndRestartApp()
            return response

        } else {
            val newRequestBuilder = response.request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")

            userDataStore.getUserSessionId()?.let { sessionId ->
                newRequestBuilder.header(SESSION_ID, sessionId)
                Log.d(TAG, "Refreshing session ID: $sessionId") // Log at DEBUG level
            }

            userDataStore.getAuthToken()?.let { authToken ->
                newRequestBuilder.header(AUTH_TOKEN, authToken)
            }

            return chain.proceed(newRequestBuilder.build())
        }
    }

    private fun logInvalidSessionDetails(response: Response) {
        Log.e(
            TAG,
            """
                API URL: ${response.request.url}
                AUTH TOKEN: ${response.request.header(AUTH_TOKEN)}
                SESSION ID: ${response.request.header(SESSION_ID)}
                CODE: ${response.code}
            """.trimIndent()
        )
    }
}