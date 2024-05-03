package com.app.ecarepro.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class AndroidDownloader(context: Context) : Downloader {

    private var request: DownloadManager.Request? = null
    private var fileName: String? = null

    private val _downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String, downloadType: String, mimeType: String): Long {

        if (url!=null){
            fileName = url.substring(url.lastIndexOf('/') + 1, url.length)

            request = DownloadManager.Request(url.toUri())
                .setMimeType(mimeType)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(fileName)
                .setDescription(downloadType)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/eCarePro Download/$downloadType/$fileName"
                )
        }


        return _downloadManager.enqueue(request)
    }


}