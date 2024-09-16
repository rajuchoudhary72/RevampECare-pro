package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.photo_setting.AlbumSetting

data class NetworkVideoAlbumDTL(
    val description: String,
    val errorCode: Int,
    val eventDate: String,
    val message: String,
    val status: String,
    val title: String,
    val totalVideos: Int,
    val videos: List<Video>,
    val setting: AlbumSetting
)