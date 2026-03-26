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
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        val authToken = if (isPublicEndpoint(request.url.encodedPath)) {
            DEFAULT_AUTH_TOKEN
        } else {
            runBlocking { tokeProvider.getAuthToken() }
        }
        requestBuilder.header(KEY_AUTH_TOKEN, authToken)
        return chain.proceed(requestBuilder.build())
    }

    private fun isPublicEndpoint(path: String): Boolean =
        PUBLIC_ENDPOINTS.any { path.contains(it, ignoreCase = true) }

    companion object {
        private const val KEY_AUTH_TOKEN = "AuthToken"
        private const val DEFAULT_AUTH_TOKEN = "Kq4IYAuSXLh4EsnexoTSfA=="
        private val PUBLIC_ENDPOINTS = listOf(
            "School/DTL",
            "School/WalkThrough",
            "School/List",
            "User/TwoFactorLogin",
            "User/ResendOTP",
            "User/ValidateOTP",
        )
    }
}