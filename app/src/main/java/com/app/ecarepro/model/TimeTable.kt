package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeTable(
    val className: String,
    val period: Int,
    val subject: String,
    val time: String,
    val teachBy: String,
):Parcelable