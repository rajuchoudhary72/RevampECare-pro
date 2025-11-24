package com.app.ecarepro.core.download.model


data class DownloadRequest(
    val url: String,
    val fileName: String,
    val mimeType: String? = null,
    val destination: Destination = Destination.ExternalPublicDir
) {
    sealed class Destination {
        data class Custom(val path: String) : Destination()
        data object ExternalPublicDir : Destination()
        data object AppPrivateFiles : Destination()
    }
}