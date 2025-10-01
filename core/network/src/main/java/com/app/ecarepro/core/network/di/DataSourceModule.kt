package com.app.ecarepro.core.network.di

import com.app.ecarepro.core.network.SchoolRemoteDataSource
import com.app.ecarepro.core.network.datasource.SchoolRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
     abstract fun bindsSchoolRemoteDataSource(
        topicsRepository: SchoolRemoteDataSourceImpl,
    ): SchoolRemoteDataSource

}