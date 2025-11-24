package com.app.ecarepro.core.download.model

sealed class DownloadStatus {
    object Started : DownloadStatus()

    data class Progress(
        val downloadId: Long,
        val percent: Int
    ) : DownloadStatus()

    data class Completed(
        val downloadId: Long,
        val fileUri: String,
        val filePath: String
    ) : DownloadStatus()

    data class Failed(
        val downloadId: Long,
        val reason: String
    ) : DownloadStatus()

    data class Cancelled(val downloadId: Long) : DownloadStatus()
}
