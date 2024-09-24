package com.app.ecarepro.model.photo_setting

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlbumSetting(
    val isAddFavouriteEnabled: Boolean?,
    val isLikeEnabled: Boolean?,
    val isShareEnabled: Boolean?
) : Parcelable