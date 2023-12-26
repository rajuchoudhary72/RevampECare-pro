package com.app.ecarepro.di

import com.app.ecarepro.data.UserRepositoryImpl
import com.app.ecarepro.data.database.UserDatabaseImpl
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class DatabaseBinds {
    @Binds
    abstract fun bindUserDatabase(
        impl: UserDatabaseImpl
    ): UserDatabase
}