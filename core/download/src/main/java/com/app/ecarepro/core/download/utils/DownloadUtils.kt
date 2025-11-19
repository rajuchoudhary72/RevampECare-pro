package com.app.ecarepro.core.download.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

object DownloadUtils {

    fun getRealPath(context: Context, uri: Uri): String {
        return when (uri.scheme) {
            "file" -> uri.path ?: ""
            "content" -> {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val fileName = it.getString(index)
                        val file = File(context.cacheDir, fileName)
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        file.absolutePath
                    } else ""
                } ?: ""
            }
            else -> ""
        }
    }
}
