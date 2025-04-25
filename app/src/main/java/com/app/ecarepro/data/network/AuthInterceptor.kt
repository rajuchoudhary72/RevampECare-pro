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
        "School/List",
        "User/GetCredentials",
        "User/GetUsernameByUID",
        "User/TwoFactorLogin",
        "User/ResendOTP",
        "User/ValidateOTP",
    )
    private val lmsApis = mutableListOf(
        "Workspace/Layout",
        "Skills/Categories",
        "Skills/All",
        "Skills/DeleteSkill",
        "Skills/Types",
        "Skills/SaveSkill",
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept", "application/json")

        val isLoginApi = loginApis.any {
            it.contains(
                chain.request().url.pathSegments.take(2).joinToString("/")
            )
        }
        val isLmsApi = lmsApis.any {
            it.contains(
                chain.request().url.pathSegments.take(2).joinToString("/")
            )
        }

        val authToken = runBlocking {
            if (isLoginApi) {
                Constant.AUTH_BEFORE_LOGIN_NEW
            } else
                userDataStore.getAuthToken() ?: Constant.AUTH_BEFORE_LOGIN_NEW
        }

        Log.e(if (isLmsApi) AUTH_KEY else AUTH_TOKEN, authToken)
        requestBuilder.addHeader(if (isLmsApi) AUTH_KEY else AUTH_TOKEN, authToken)


        return chain.proceed(requestBuilder.build())
    }

    companion object {
        const val AUTH_TOKEN = "AuthToken"
        const val AUTH_KEY = "AuthKey"
    }


}