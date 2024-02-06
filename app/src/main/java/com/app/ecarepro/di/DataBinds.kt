package com.app.ecarepro.di

import com.app.ecarepro.data.SchoolRepositoryImpl
import com.app.ecarepro.data.UserRepositoryImpl
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class DataBinds {
    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindSchoolRepository(
        impl: SchoolRepositoryImpl
    ): SchoolRepository



}