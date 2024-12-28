package com.app.ecarepro.utils

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.net.toUri

class AndroidDownloader(val context: Context) : Downloader {

    private var fileName: String? = null

    private val _downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String, downloadType: String, mimeType: String): Long {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                context,
                "External storage permission is required to download files. GO to setting to enable permission",
                Toast.LENGTH_SHORT
            ).show()
            return -1
        }

        fileName = url.substring(url.lastIndexOf('/') + 1, url.length)
        val request = DownloadManager.Request(url.toUri())
            .setMimeType(mimeType)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
            .setTitle(fileName)
            .setDescription(downloadType)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "/eCarePro Download/$downloadType/$fileName"
            )

        return _downloadManager.enqueue(request)
    }


}