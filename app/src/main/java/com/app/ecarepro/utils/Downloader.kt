package com.app.ecarepro.utils

interface Downloader {
    fun downloadFile(url:String,downloadType:String):Long

}