package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivityMonth(
    val activity: List<Activity>?,
    val isCurrent: Boolean?,
    val monthName: String?,
    val monthNo: Int?,
    val year: Int?
): Parcelable