package com.app.ecarepro.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TeacherTimetable(
    val data: List<TimetableData>?,
    val displayToday: Boolean?,
)

@Serializable
data class TimetableData(
    val day: String?,
    val dayNo: Int?,
    val timeTable: List<Timetable>?,
)

@Serializable
data class Timetable(
    val className: String?,
    val period: Int?,
    val subject: String?,
    val time: String?,
    val isCurrent: Boolean? = false,
    val type: String? = null,
    val details: String? = null,
    val duration: String? = null,
)


