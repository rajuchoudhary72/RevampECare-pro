package com.app.ecarepro.core.location.di

import android.content.Context
import com.app.ecarepro.core.domain.location.LocationProvider
import com.app.ecarepro.core.location.FusedLocationProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    abstract fun bindLocationProvider(
        impl: FusedLocationProviderImpl
    ): LocationProvider
}