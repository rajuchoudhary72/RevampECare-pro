package com.app.ecarepro.core.database.di

import com.app.ecarepro.core.database.EcareProDatabase
import com.app.ecarepro.core.database.dao.SchoolDao
import com.app.ecarepro.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Singleton
    @Provides
    fun providesSchoolDao(
        database: EcareProDatabase,
    ): SchoolDao = database.schoolDao()

    @Singleton
    @Provides
    fun providesUserDao(
        database: EcareProDatabase,
    ): UserDao = database.userDao()

}