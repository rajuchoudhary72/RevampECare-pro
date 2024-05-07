package com.app.ecarepro.di

import com.app.ecarepro.data.database.UserDatabaseImpl
import com.app.ecarepro.data.database.databases.SchoolDatabase
import com.app.ecarepro.data.database.databases.UserDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseBinds {
    @Binds
    abstract fun bindUserDatabase(
        impl: UserDatabaseImpl
    ): UserDatabase

    @Binds
    abstract fun bindSchoolDatabase(
        impl: UserDatabaseImpl
    ): SchoolDatabase
}