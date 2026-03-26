package com.app.ecarepro.core.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment.DIRECTORY_DOWNLOADS
import com.app.ecarepro.core.download.model.DownloadRequest
import com.app.ecarepro.core.download.model.DownloadStatus
import com.app.ecarepro.core.download.utils.DownloadUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class FileDownloaderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : FileDownloader {

    private val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    override fun download(request: DownloadRequest): Flow<DownloadStatus> = flow {

        val downloadId = enqueue(request)
        emit(DownloadStatus.Started)

        var isDownloading = true

        while (isDownloading) {
            delay(400) // smoother progress

            val cursor = dm.query(DownloadManager.Query().setFilterById(downloadId))
            if (!cursor.moveToFirst()) {
                cursor.close()
                continue
            }

            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))

            when (status) {

                DownloadManager.STATUS_RUNNING -> {
                    val total =
                        cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val downloaded =
                        cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                    val percent = if (total > 0) ((downloaded * 100) / total).toInt() else 0

                    emit(DownloadStatus.Progress(downloadId, percent))
                }

                DownloadManager.STATUS_SUCCESSFUL -> {
                    val uriStr =
                        cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))

                    val path = DownloadUtils.getRealPath(context, Uri.parse(uriStr))

                    emit(DownloadStatus.Completed(downloadId, uriStr, path))
                    isDownloading = false
                }

                DownloadManager.STATUS_FAILED -> {
                    val reason =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))

                    emit(DownloadStatus.Failed(downloadId, "Failed: $reason"))
                    isDownloading = false
                }
            }

            cursor.close()
        }
    }

    override fun cancel(downloadId: Long) {
        dm.remove(downloadId)
    }

    override fun isFileDownloaded(fileName: String): Boolean {
        val file = java.io.File(context.getExternalFilesDir(null), fileName)
        return file.exists()
    }

    override fun getDownloadedFilePath(fileName: String): String {
        return java.io.File(context.getExternalFilesDir(null), fileName).absolutePath
    }

    private fun enqueue(request: DownloadRequest): Long {
        val req = DownloadManager.Request(Uri.parse(request.url))
            .setTitle(request.fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        when (request.destination) {
            DownloadRequest.Destination.ExternalPublicDir ->
                req.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, request.fileName)

            DownloadRequest.Destination.AppPrivateFiles ->
                req.setDestinationInExternalFilesDir(context, null, request.fileName)

            is DownloadRequest.Destination.Custom ->
                req.setDestinationUri(Uri.parse(request.destination.path))
        }

        request.mimeType?.let { req.setMimeType(it) }

        return dm.enqueue(req)
    }
}
