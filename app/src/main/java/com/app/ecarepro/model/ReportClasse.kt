package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportClasse(
    val academicYear: String?,
    val classID: Int?,
    val className: String?,
    val isCur: Int?,
    val reportCards: List<ReportCard>?,
    val yrID: Int?
):Parcelable