package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mark(
    val examName: String?,
    val marksObtained: String?,
    val maximumMarks: String?
):Parcelable