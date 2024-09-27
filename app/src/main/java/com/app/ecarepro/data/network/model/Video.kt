package com.app.ecarepro.data.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val id: String?,
    val isFavourite: Boolean?,
    val isLike: Boolean?,
    val likes: Int?,
    val title: String?,
    val url: String?
) : Parcelable