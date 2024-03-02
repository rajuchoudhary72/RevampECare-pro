package com.app.ecarepro.di

import android.content.Context
import com.app.ecarepro.data.network.AuthInterceptor
import com.app.ecarepro.data.network.service.MessageService
import com.app.ecarepro.data.network.service.SchoolService
import com.app.ecarepro.data.network.service.UserService
import com.app.ecarepro.utils.Constant
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun provideUserService(
        retrofit: Retrofit
    ): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    fun provideSchoolService(
        retrofit: Retrofit
    ): SchoolService {
        return retrofit.create(SchoolService::class.java)
    }

    @Provides
    fun provideMessageService(
        retrofit: Retrofit
    ): MessageService {
        return retrofit.create(MessageService::class.java)
    }



}