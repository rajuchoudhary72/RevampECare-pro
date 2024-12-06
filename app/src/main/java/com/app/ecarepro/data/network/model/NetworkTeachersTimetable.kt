package com.app.ecarepro.data.network.model

import android.os.Parcelable
import com.app.ecarepro.model.TimeTableData
import kotlinx.parcelize.Parcelize

@Parcelize
data class NetworkTeachersTimetable(
    val `data`: List<TimeTableData>,
    val dispalyToday: Boolean,
    val errorCode: Int,
    val message: String,
    val status: String
): Parcelable