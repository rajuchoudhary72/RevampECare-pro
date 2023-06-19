package com.app.ecarepro.data.network

import com.app.ecarepro.data.datastore.UserDataStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userDataStore: UserDataStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("accept", "application/json")
        requestBuilder.addHeader("Authorization", "Bearer ")

        return chain.proceed(requestBuilder.build())
    }
}