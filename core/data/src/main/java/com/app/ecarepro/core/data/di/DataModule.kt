package com.app.ecarepro.core.data.di

import com.app.ecarepro.core.data.repository.SchoolRepositoryImpl
import com.app.ecarepro.core.domain.repository.SchoolRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsSchoolRepository(
        topicsRepository: SchoolRepositoryImpl,
    ): SchoolRepository

}