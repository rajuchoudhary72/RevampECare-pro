package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class AlbumType(
    val typeID: Int,
    val typeName: String
) : Parcelable