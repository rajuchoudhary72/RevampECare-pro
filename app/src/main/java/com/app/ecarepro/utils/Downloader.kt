package com.app.ecarepro.utils

interface Downloader {
    fun downloadFile(
        url: String,
        downloadType: String,
        mimeType: String = Constant.PDF_Mime_Type
    ): Long

}