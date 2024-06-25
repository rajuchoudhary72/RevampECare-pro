package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AlbumType

data class NetworkAlbumType(
    val albumTypes: List<AlbumType>,
    val errorCode: Int,
    val message: String,
    val status: String
)