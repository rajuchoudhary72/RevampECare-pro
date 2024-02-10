package com.app.ecarepro.data.network

import android.content.Context
import android.util.Log
import com.app.ecarepro.R
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.utils.Constant
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class AuthInterceptor @Inject constructor(
    @ApplicationContext val context: Context,
    private val userDataStore: UserDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept", "application/json")

        val authToken = runBlocking { userDataStore.getAuthToken() ?: Constant.AUTH_BEFORE_LOGIN }

        Log.e(AUTH_TOKEN, authToken)
        requestBuilder.addHeader(AUTH_TOKEN, authToken)

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        const val AUTH_TOKEN = "AuthToken"
    }
}