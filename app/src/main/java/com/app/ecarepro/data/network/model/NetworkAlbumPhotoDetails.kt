package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Photo

data class NetworkAlbumPhotoDetails(
    val description: String,
    val errorCode: Int,
    val eventDate: String,
    val message: String,
    val photos: List<Photo>,
    val status: String,
    val title: String,
    val totalPhotos: Int
)