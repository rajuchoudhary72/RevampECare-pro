package com.app.ecarepro.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.app.ecarepro.data.cache.JsonCache
import com.app.ecarepro.data.database.ECareProDatabase
import com.app.ecarepro.data.database.MIGRATION_4_5
import com.app.ecarepro.data.database.MIGRATION_5_6
import com.app.ecarepro.data.database.dao.LocalizationDao
import com.app.ecarepro.data.database.dao.SchoolDao
import com.app.ecarepro.data.database.dao.UserDao
import com.app.ecarepro.ui.language.LanguageRepository
import com.app.ecarepro.ui.language.dynamic_language.preferences.LocalizationPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideECareProDatabase(
        @ApplicationContext applicationContext: Context
    ): ECareProDatabase {
        return Room.databaseBuilder(
            applicationContext,
            ECareProDatabase::class.java, "ecare-database"
        )
            .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
            .build()
    }

    @Provides
    fun provideUserDao(
        database: ECareProDatabase
    ): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideSchoolDao(
        database: ECareProDatabase
    ): SchoolDao {
        return database.schoolDao()
    }


    @Provides
    @Singleton
    fun provideJsonCache(): JsonCache {
        return JsonCache()
    }

    @Provides
    @Singleton
    fun provideLanguageRepository(@ApplicationContext context: Context): LanguageRepository {
        return LanguageRepository(context)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("app_preferences")
        }
    }

    @Provides
    @Singleton
    fun provideLocalizationPreferences(dataStore: DataStore<Preferences>): LocalizationPreferences {
        return LocalizationPreferences(dataStore)
    }

    @Provides
    @Singleton
    fun provideLocalizationDatabase(@ApplicationContext context: Context): ECareProDatabase {
        return Room.databaseBuilder(
            context,
            ECareProDatabase::class.java,
            "localization_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocalizationDao(database: ECareProDatabase): LocalizationDao {
        return database.localizationDao()
    }

}