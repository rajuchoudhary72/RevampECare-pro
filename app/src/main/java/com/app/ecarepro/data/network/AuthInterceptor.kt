package com.app.ecarepro.data.network

import android.content.Context
import android.util.Log
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    @ApplicationContext val context: Context,
    private val userDataStore: UserDataStore
) : Interceptor {
    private val loginApis = mutableListOf(
        "School/DTL",
        "User/Verify",
        "User/Login",
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept", "application/json")

        val isLoginApi = loginApis.any { it.contains(chain.request().url.pathSegments.last()) }

        val authToken = runBlocking {
            if (isLoginApi) {
                Constant.AUTH_BEFORE_LOGIN
            } else
                userDataStore.getAuthToken() ?: Constant.AUTH_BEFORE_LOGIN
        }

        Log.e(AUTH_TOKEN, authToken)
        requestBuilder.addHeader(AUTH_TOKEN, authToken)

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        const val AUTH_TOKEN = "AuthToken"
    }


}