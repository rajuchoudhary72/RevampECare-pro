package com.app.ecarepro.di

import com.app.ecarepro.data.FomApiRepositoryImpl
import com.app.ecarepro.data.MessageRepositoryImpl
import com.app.ecarepro.data.SchoolRepositoryImpl
import com.app.ecarepro.data.UserRepositoryImpl
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.data.repository.LocalizationRepository
import com.app.ecarepro.data.repository.LocalizationRepositoryImp
import com.app.ecarepro.data.repository.MessageRepository
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

    @Binds
    abstract fun bindMessageRepository(
        impl: MessageRepositoryImpl
    ): MessageRepository

    @Binds
    abstract fun bindFomApiRepository(
        impl: FomApiRepositoryImpl
    ): FomApiRepository

    @Binds
    abstract fun LocalizationRepository(
        impl: LocalizationRepositoryImp
    ): LocalizationRepository


}