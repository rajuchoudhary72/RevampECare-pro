package com.app.ecarepro.core.network.retrofit.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private const val DEFAULT_AUTH_TOKEN = "Kq4IYAuSXLh4EsnexoTSfA=="

internal class AuthTokenInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.header(KEY_AUTH_TOKEN, DEFAULT_AUTH_TOKEN)
        return chain.proceed(requestBuilder.build())
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "AuthToken"

    }

}