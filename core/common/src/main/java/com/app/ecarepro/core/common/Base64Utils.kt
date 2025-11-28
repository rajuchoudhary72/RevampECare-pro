package com.app.ecarepro.core.common


import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

object Base64Utils {

    fun getBase64StringFromUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream = requireNotNull(context.contentResolver.openInputStream(uri))
            val bytes = readBytes(inputStream)
            Base64.encodeToString(
                bytes,
                Base64.NO_WRAP
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getBase64StringFromFile(file: File): String? {
        return try {
            val inputStream = FileInputStream(file)
            val bytes = readBytes(inputStream)
            Base64.encodeToString(
                bytes,
                Base64.NO_WRAP
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getFileExtension(file: File): String {
        val name = file.name
        val lastIndexOf = name.lastIndexOf(".")
        if (lastIndexOf == -1) {
            return ""
        }
        return name.substring(lastIndexOf + 1)
    }

    @Throws(IOException::class)
    private fun readBytes(inputStream: InputStream): ByteArray {
        // Use block usage for auto-closing streams
        inputStream.use { input ->
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)

            var len: Int
            while (input.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            return byteBuffer.toByteArray()
        }
    }
}
