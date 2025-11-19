package com.app.ecarepro.core.download


import com.app.ecarepro.core.download.model.DownloadRequest
import com.app.ecarepro.core.download.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

interface FileDownloader {

    fun download(request: DownloadRequest): Flow<DownloadStatus>

    fun cancel(downloadId: Long)
}
