package com.app.ecarepro.model

import android.os.Parcelable
import com.app.ecarepro.model.photo_setting.AlbumSetting
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    val description: String?,
    val isFavourite: Boolean?,
    val isLike: Boolean?,
    val likes: Int?,
    val id: String?,
    val photoPath: String?,
    val title: String?,
 ) : Parcelable