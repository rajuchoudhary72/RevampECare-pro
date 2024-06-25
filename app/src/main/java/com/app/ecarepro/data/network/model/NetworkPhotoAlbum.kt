package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Album

data class NetworkPhotoAlbum(
    val albums: List<Album>,
    val errorCode: Int,
    val message: String,
    val status: String
)