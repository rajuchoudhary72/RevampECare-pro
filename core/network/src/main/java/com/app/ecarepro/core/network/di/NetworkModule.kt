package com.app.ecarepro.core.network.di

import com.app.ecarepro.core.domain.model.AppConfig
import com.app.ecarepro.core.network.retrofit.interceptor.AuthTokenInterceptor
import com.app.ecarepro.core.network.retrofit.service.AppService
import com.app.ecarepro.core.network.retrofit.service.AcademicService
import com.app.ecarepro.core.network.retrofit.service.AdminService
import com.app.ecarepro.core.network.retrofit.service.AnnouncementService
import com.app.ecarepro.core.network.retrofit.service.GalleryService
import com.app.ecarepro.core.network.retrofit.service.SchoolService
import com.app.ecarepro.core.network.retrofit.service.StaffService
import com.app.ecarepro.core.network.retrofit.service.TaskManagerService
import com.app.ecarepro.core.network.retrofit.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton
import com.app.ecarepro.core.network.retrofit.service.DashboardService
import com.app.ecarepro.core.network.retrofit.service.DisciplineService
import com.app.ecarepro.core.network.retrofit.service.GlobalSearchService
import com.app.ecarepro.core.network.retrofit.service.LibraryService
import com.app.ecarepro.core.network.retrofit.service.MessageService
import com.app.ecarepro.core.network.retrofit.service.ReportService
import com.app.ecarepro.core.network.retrofit.service.SmsService
import com.app.ecarepro.core.network.retrofit.service.SurveyService
import com.app.ecarepro.core.network.retrofit.service.TransportService
import java.util.concurrent.TimeUnit


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
        appConfig: AppConfig,
    ): Retrofit {
        val okHttpClient = OkHttpClient
            .Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
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
        @InjectInCoreModule retrofit: Retrofit,
    ): SchoolService = retrofit.create(SchoolService::class.java)

    @Provides
    fun provideUserService(
        @InjectInCoreModule retrofit: Retrofit,
    ): UserService = retrofit.create(UserService::class.java)

    @Provides
    fun provideAcademicService(
        @InjectInCoreModule retrofit: Retrofit,
    ): AcademicService = retrofit.create(AcademicService::class.java)

    @Provides
    fun provideAdminService(
        @InjectInCoreModule retrofit: Retrofit,
    ): AdminService = retrofit.create(AdminService::class.java)

    @Provides
    fun provideStaffService(
        @InjectInCoreModule retrofit: Retrofit,
    ): StaffService = retrofit.create(StaffService::class.java)
    @Provides
    fun provideSmsService(
        @InjectInCoreModule retrofit: Retrofit,
    ): SmsService = retrofit.create(SmsService::class.java)

    @Provides
    fun provideTaskManagerService(
        @InjectInCoreModule retrofit: Retrofit,
    ): TaskManagerService = retrofit.create(TaskManagerService::class.java)

    @Provides
    fun provideDisciplineService(
        @InjectInCoreModule retrofit: Retrofit,
    ): DisciplineService = retrofit.create(DisciplineService::class.java)

    @Provides
    fun provideTransportService(
        @InjectInCoreModule retrofit: Retrofit,
    ): TransportService = retrofit.create(TransportService::class.java)

    @Provides
    fun provideAnnouncementService(
        @InjectInCoreModule retrofit: Retrofit,
    ): AnnouncementService = retrofit.create(AnnouncementService::class.java)

    @Provides

    fun provideGalleryService(
        @InjectInCoreModule retrofit: Retrofit,
    ): GalleryService = retrofit.create(GalleryService::class.java)


    @Provides
    fun provideAppService(
        @InjectInCoreModule retrofit: Retrofit,
    ): AppService = retrofit.create(AppService::class.java)

     @Provides
    fun provideMessageService(
        @InjectInCoreModule retrofit: Retrofit,
    ): MessageService = retrofit.create(MessageService::class.java)

    @Provides
    fun provideLibraryService(
        @InjectInCoreModule retrofit: Retrofit,
    ): LibraryService = retrofit.create(LibraryService::class.java)

    @Provides

    fun provideReportService(
        @InjectInCoreModule retrofit: Retrofit,
    ): ReportService = retrofit.create(ReportService::class.java)

    @Provides
    @Singleton
    fun provideSurveyService(
        @InjectInCoreModule retrofit: Retrofit,
    ): SurveyService = retrofit.create(SurveyService::class.java)

    @Provides
    fun provideDashboardService(
        @InjectInCoreModule retrofit: Retrofit,
    ): DashboardService = retrofit.create(DashboardService::class.java)

    @Provides
    fun provideGlobalSearchService(
        @InjectInCoreModule retrofit: Retrofit,
    ): GlobalSearchService = retrofit.create(GlobalSearchService::class.java)


}

