package com.app.ecarepro.core.database.di

import android.content.Context
import androidx.room.Room
import com.app.ecarepro.core.database.EcareProDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesEcareProDatabase(
        @ApplicationContext context: Context,
    ): EcareProDatabase = Room.databaseBuilder(
        context = context,
        klass = EcareProDatabase::class.java,
        name = "ecarepro-database"
    ).build()
}