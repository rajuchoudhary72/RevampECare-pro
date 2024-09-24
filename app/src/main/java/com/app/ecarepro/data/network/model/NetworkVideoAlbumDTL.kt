package com.app.ecarepro.data.network.model

import android.os.Parcelable
import com.app.ecarepro.model.photo_setting.AlbumSetting
import kotlinx.android.parcel.Parcelize

@Parcelize
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
) : Parcelable