package com.app.ecarepro.data.network

import android.util.Log
import com.app.ecarepro.data.AppSessionManager
import com.app.ecarepro.data.network.AuthInterceptor.Companion.AUTH_TOKEN

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class InvalidSessionInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 403) {
            Log.e("InvalidSessionInterceptor", "API URL ("+response.request.url.toString()+") \n AUTH TOKEN ("+response.request.header(AUTH_TOKEN)+") \n CODE ("+response.code+")")
            AppSessionManager.logoutAndRestartApp()

        }

        return response
    }
}

