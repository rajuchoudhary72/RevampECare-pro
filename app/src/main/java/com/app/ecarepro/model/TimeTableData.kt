package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeTableData(
    val day: String,
    val dayNo: Int,
    val timeTable: List<TimeTable>
) : Parcelable