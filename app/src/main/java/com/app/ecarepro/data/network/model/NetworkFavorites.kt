package com.app.ecarepro.data.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NetworkFavorites(
    val errorCode: Int?,
    val list: List<FavList>?,
    val message: String?,
    val status: String?
) : Parcelable