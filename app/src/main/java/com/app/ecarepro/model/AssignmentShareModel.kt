package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AssignmentShareModel(
    val asgFiles: List<String>? ,
    val hasAttachment: Boolean?,
): Parcelable
