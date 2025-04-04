package com.app.ecarepro.ui.gallery.kid_corner.model

data class NetworkKidsAlbumDetailsModel(
    val albumDetail: AlbumDetail,
    val albumDetails: List<AlbumDetailX>,
    val errorCode: Int,
    val message: String,
    val status: String
)