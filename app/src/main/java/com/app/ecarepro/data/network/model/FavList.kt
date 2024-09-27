package com.app.ecarepro.data.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FavList (
    val fileName: String?,
    val galleryType: Int?,
    val id: String?,
    val isFavourite: Boolean?,
    val islLike: Int?,
    val totalLike: Int?
) : Parcelable