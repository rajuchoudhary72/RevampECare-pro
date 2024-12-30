package com.app.ecarepro.di

import android.content.Context
import com.app.ecarepro.BuildConfig
import com.app.ecarepro.data.network.AuthInterceptor
import com.app.ecarepro.data.network.intercepter.ConnectivityInterceptor
import com.app.ecarepro.data.network.intercepter.CustomResponseInterceptor
import com.app.ecarepro.data.network.service.AppService
import com.app.ecarepro.data.network.service.FomApiService
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
        authInterceptor: AuthInterceptor,
        connectivityInterceptor: ConnectivityInterceptor,
        customResponseInterceptor: CustomResponseInterceptor

    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(connectivityInterceptor)
            .addInterceptor(customResponseInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }
/* .addInterceptor(connectivityInterceptor)
            .addInterceptor(customResponseInterceptor)*/
@Provides
fun provideRetrofit(
    okHttpClient: OkHttpClient,
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(
            if (BuildConfig.FLAVOR == "dev") {
                Constant.BASE_DEV_URL
            } else {
                Constant.BASE_URL
            }
        )
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

    @Provides
    fun provideAppService(
        retrofit: Retrofit
    ): AppService {
        return retrofit.create(AppService::class.java)
    }

    @Provides
    fun provideFomApiService(
        retrofit: Retrofit
    ): FomApiService {
        return retrofit.create(FomApiService::class.java)
    }

}