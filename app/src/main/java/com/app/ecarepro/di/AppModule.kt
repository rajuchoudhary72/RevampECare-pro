package com.app.ecarepro.di

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings.Secure
import com.app.ecarepro.BuildConfig
import com.app.ecarepro.core.domain.model.AppConfig
import com.app.ecarepro.core.domain.model.DeviceType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @SuppressLint("HardwareIds")
    @Provides
    @Singleton
    fun provideAppConfig(@ApplicationContext context: Context): AppConfig {
        val deviceId = Secure.getString(
            context.contentResolver,
            Secure.ANDROID_ID
        )

        val baseUrl = if (BuildConfig.BUILD_TYPE.equals("release", true)) {
            "https://androidapi.franciscanecare.net/"
        } else {
            "https://apiuat.franciscanecare.net/"
        }
        return AppConfig(
            baseUrl = baseUrl,
            deviceId = deviceId,
            appVersion = BuildConfig.VERSION_NAME,
            deviceType = DeviceType.ANDROID,
            deviceModel = Build.MODEL,
            applicationId = BuildConfig.APPLICATION_ID,
            isDebug = BuildConfig.DEBUG,
            appVersionCode = BuildConfig.VERSION_CODE,
            osVersion =Build.VERSION.RELEASE
        )
    }
}