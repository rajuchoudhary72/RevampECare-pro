package com.app.ecarepro.core.data.di

import com.app.ecarepro.core.data.auth.TokenProviderImpl
import com.app.ecarepro.core.domain.auth.TokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenProviderModule {

    @Binds
    @Singleton
    abstract fun bindTokenProvider(
        defaultTokenProvider: TokenProviderImpl,
    ): TokenProvider
}