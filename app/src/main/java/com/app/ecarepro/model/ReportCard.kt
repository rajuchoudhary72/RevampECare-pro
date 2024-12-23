package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportCard(
    val backFileName: String?,
    val backFileSize: String?,
    val examName: String?,
    val fileName: String?,
    val fileSize: String?,
    val frontFileName: String?,
    val frontFileSize: String?,
    val updatedOn: String?,
    val viewMode: Int?
): Parcelable