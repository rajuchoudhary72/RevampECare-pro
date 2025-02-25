package com.app.ecarepro.di

import android.content.Context
import androidx.room.Room
import com.app.ecarepro.data.cache.JsonCache
import com.app.ecarepro.data.database.ECareProDatabase
import com.app.ecarepro.data.database.MIGRATION_4_5
import com.app.ecarepro.data.database.MIGRATION_5_6
import com.app.ecarepro.data.database.MIGRATION_8_9
import com.app.ecarepro.data.database.dao.SchoolDao
import com.app.ecarepro.data.database.dao.UserDao
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
            .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_8_9)
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

}