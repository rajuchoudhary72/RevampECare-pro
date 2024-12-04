package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Activity(
    val duration: Int?,
    val fromDate: String?,
    val isWorking: Boolean?,
    val tillDate: String?,
    val title: String?
):Parcelable