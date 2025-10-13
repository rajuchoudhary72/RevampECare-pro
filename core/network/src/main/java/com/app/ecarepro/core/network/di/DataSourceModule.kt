package com.app.ecarepro.core.network.di

import com.app.ecarepro.core.network.SchoolRemoteDataSource
import com.app.ecarepro.core.network.UserRemoteDataSource
import com.app.ecarepro.core.network.datasource.SchoolRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
     abstract fun bindsSchoolRemoteDataSource(
        dataSource: SchoolRemoteDataSourceImpl,
    ): SchoolRemoteDataSource

    @Binds
     abstract fun bindsUserRemoteDataSource(
        dataSource: UserRemoteDataSourceImpl,
    ): UserRemoteDataSource

}