package com.app.ecarepro.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings.Secure
import com.app.ecarepro.data.AppSessionManager
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.AuthInterceptor.Companion.SESSION_ID
import com.app.ecarepro.data.network.service.UserService
import com.app.ecarepro.di.annotations.SessionReCreate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class SessionAuthenticator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore,
    @SessionReCreate private val userService: UserService
) : Authenticator {

    @SuppressLint("HardwareIds")
    override fun authenticate(route: Route?, response: Response): Request? {
        return synchronized(this) {
            runBlocking {
                try {
                    val sessionID = userService.createSession(
                        CreateUserSessionRequestDto(
                            ipAddress = Secure.getString(
                                context.contentResolver,
                                Secure.ANDROID_ID
                            ),
                            locationCity = userDataStore.getCityName(),
                            oldSessionID = userDataStore.getUserSessionId()
                        )
                    ).sessionID
                    userDataStore.saveSessionId(sessionID)
                    response.request.newBuilder()
                        .header(SESSION_ID, sessionID)
                        .build()
                } catch (e: Exception) {
                    AppSessionManager.logoutAndRestartApp(true)
                    null
                }
            }
        }

    }
}
