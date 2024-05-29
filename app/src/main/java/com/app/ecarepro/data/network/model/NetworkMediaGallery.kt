package com.app.ecarepro.data.network.model

data class NetworkMediaGallery(
    val albums: List<Album>,
    val errorCode: Int,
    val message: String,
    val status: String,
    val years: List<String>
)