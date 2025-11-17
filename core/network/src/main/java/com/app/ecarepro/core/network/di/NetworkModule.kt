package com.app.ecarepro.core.network.di

import com.app.ecarepro.core.domain.model.AppConfig
import com.app.ecarepro.core.network.retrofit.interceptor.AuthTokenInterceptor
import com.app.ecarepro.core.network.retrofit.service.AcademicService
import com.app.ecarepro.core.network.retrofit.service.AdminService
import com.app.ecarepro.core.network.retrofit.service.SchoolService
import com.app.ecarepro.core.network.retrofit.service.UserService
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
import javax.inject.Inject
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
        appConfig: AppConfig
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
            .baseUrl(appConfig.baseUrl)
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

    @Provides
    fun provideUserService(
        @InjectInCoreModule retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)

    @Provides
    fun provideAcademicService(
        @InjectInCoreModule retrofit: Retrofit
    ): AcademicService = retrofit.create(AcademicService::class.java)

    @Provides
    fun provideAdminService(
        @InjectInCoreModule retrofit: Retrofit
    ): AdminService = retrofit.create(AdminService::class.java)

}

