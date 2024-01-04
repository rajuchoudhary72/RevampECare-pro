package com.app.ecarepro.data.network

import android.util.Log
import com.app.ecarepro.data.datastore.UserDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userDataStore: UserDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val authToken = runBlocking { userDataStore.getAuthToken() ?: "Kq4IYAuSXLh4EsnexoTSfA==" }
        Log.e(AUTH_TOKEN, authToken)
        requestBuilder.addHeader(AUTH_TOKEN, authToken)

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        const val AUTH_TOKEN = "AuthToken"
    }
}