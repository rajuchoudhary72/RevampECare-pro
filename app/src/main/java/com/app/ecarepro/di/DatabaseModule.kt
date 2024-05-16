package com.app.ecarepro.di

import android.content.Context
import androidx.room.Room
import com.app.ecarepro.data.database.ECareProDatabase
import com.app.ecarepro.data.database.dao.SchoolDao
import com.app.ecarepro.data.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

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
        ).build()
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


}