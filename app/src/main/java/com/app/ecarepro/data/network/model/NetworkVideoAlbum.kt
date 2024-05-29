package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AlbumVideo

data class NetworkVideoAlbum(
    val albums: List<AlbumVideo>,
    val errorCode: Int,
    val message: String,
    val status: String
)