package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.TimeTableData

data class NetworkTeachersTimetable(
    val `data`: List<TimeTableData>,
    val dispalyToday: Boolean,
    val errorCode: Int,
    val message: String,
    val status: String
)