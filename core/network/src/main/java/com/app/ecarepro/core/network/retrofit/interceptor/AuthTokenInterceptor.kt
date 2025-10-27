package com.app.ecarepro.core.network.retrofit.interceptor

import com.app.ecarepro.core.domain.auth.TokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


internal class AuthTokenInterceptor @Inject constructor(
    private val tokeProvider: TokenProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val authToken =
            runBlocking { tokeProvider.getAuthToken()}
        requestBuilder.header(KEY_AUTH_TOKEN, authToken)
        return chain.proceed(requestBuilder.build())
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "AuthToken"

    }

}