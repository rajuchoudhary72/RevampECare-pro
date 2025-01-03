package com.app.ecarepro.data.network

import com.app.ecarepro.data.AppSessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class InvalidSessionInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 403) {
            AppSessionManager.logoutAndRestartApp()
        }

        return response
    }
}