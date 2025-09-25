package com.app.ecarepro.core.network.di

import com.app.ecarepro.core.network.retrofit.interceptor.AuthTokenInterceptor
import com.app.ecarepro.core.network.retrofit.service.SchoolService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }


    @InjectInCoreModule
    @Provides
    fun provideRetrofit(
        networkJson: Json,
        tokenInterceptor: AuthTokenInterceptor,
    ): Retrofit {
        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    })
            .build()
        return Retrofit.Builder()
            .baseUrl("https://apiuat.franciscanecare.net/")
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .client(okHttpClient)
            .build()
    }


    @Provides
    fun provideSchoolService(
        @InjectInCoreModule retrofit: Retrofit
    ): SchoolService = retrofit.create(SchoolService::class.java)


}

