package com.app.ecarepro.di

import com.app.ecarepro.data.AppRepositoryImpl
import com.app.ecarepro.data.datastore.SchoolDataStore
import com.app.ecarepro.data.datastore.SchoolDataStoreImpl
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.datastore.UserDataStoreImpl
import com.app.ecarepro.data.repository.AppRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreBinds {

    @Binds
    abstract fun bindUserDataStore(
        impl: UserDataStoreImpl
    ): UserDataStore

    @Binds
    abstract fun bindSchoolDataStore(
        impl: SchoolDataStoreImpl
    ): SchoolDataStore


    @Binds
    abstract fun bindAppRepository(
        impl: AppRepositoryImpl
    ): AppRepository

}