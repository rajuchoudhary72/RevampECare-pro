package com.app.ecarepro.core.download.di

import com.app.ecarepro.core.download.FileDownloader
import com.app.ecarepro.core.download.FileDownloaderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DownloadModule {
    @Binds
    internal abstract fun bindLocationProvider(
        impl: FileDownloaderImpl,
    ): FileDownloader
}