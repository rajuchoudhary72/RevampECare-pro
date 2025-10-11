package com.app.ecarepro.core.database.di

import com.app.ecarepro.core.database.EcareProDatabase
import com.app.ecarepro.core.database.dao.SchoolDao
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

}