package com.app.ecarepro

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Calendar

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Write crash log to a file
        writeCrashLogToFile(throwable)

        // Call the default handler (optional)
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun writeCrashLogToFile(throwable: Throwable) {
        try {
            // Get file path
            val crashLogFile = getLogFile()

            // Write the stack trace to the file
            val writer = FileWriter(crashLogFile, true)
            writer.append("Crash Log: ").append(System.currentTimeMillis().toString()).append("\n")
            writer.append("Stacktrace: \n")

            val printWriter = PrintWriter(writer)
            throwable.printStackTrace(printWriter)

            writer.append("\n\n")
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun currentData(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd");
        val calendar = Calendar.getInstance()
        val fileName = dateFormat.format(calendar.time)
        return fileName
    }
    private fun getLogFile(): File {
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "${BuildConfig.FLAVOR} logs ${BuildConfig.VERSION_NAME}"
        )
        if (folder.exists().not()) {
            folder.mkdirs()
            folder.createNewFile()
        }
        val fileName = currentData()

        val file = File(folder.path + File.separator + fileName + ".txt")

        if (file.exists().not())
            file.createNewFile()
        return file
    }

    private fun getCrashLogFile(): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (!dir?.exists()!!) {
            dir.mkdirs()
        }

        // Create a file to store the crash logs
        return File(dir, "crash_log.txt")
    }
}