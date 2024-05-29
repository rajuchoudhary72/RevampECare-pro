package com.app.ecarepro.data.network.model

data class NetworkVideoAlbumDTL(
    val description: String,
    val errorCode: Int,
    val eventDate: String,
    val message: String,
    val status: String,
    val title: String,
    val totalVideos: Int,
    val videos: List<Video>
)