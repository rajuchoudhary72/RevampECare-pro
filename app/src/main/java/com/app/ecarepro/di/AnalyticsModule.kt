package com.app.ecarepro.di

import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import com.app.ecarepro.ui.firebaseAnalytics.AppAnalyticsManager
import com.app.ecarepro.ui.firebaseAnalytics.providers.GoogleAnalyticsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {


    @Provides
    @Singleton
    fun provideLoggingInterceptor(
        googleAnalyticsService: GoogleAnalyticsService
    ): AnalyticsManager {
        return AppAnalyticsManager().apply {
            initialize(googleAnalyticsService)
        }
    }
}