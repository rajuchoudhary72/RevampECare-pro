package com.app.ecarepro.core.network.model.academic

import com.app.ecarepro.core.domain.model.TeacherTimetable
import com.app.ecarepro.core.domain.model.Timetable
import com.app.ecarepro.core.domain.model.TimetableData
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTeacherTimetable(
    @SerialName("data")
    val data: List<NetworkTimetableData>?,
    @SerialName("dispalyToday")
    val displayToday: Boolean?,
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
) : NetworkResponse

@Serializable
data class NetworkTimetableData(
    @SerialName("day")
    val day: String?,
    @SerialName("dayNo")
    val dayNo: Int?,
    @SerialName("timeTable")
    val timeTable: List<NetworkTimeTable>?,
)

@Serializable
data class NetworkTimeTable(
    @SerialName("className")
    val className: String?,
    @SerialName("period")
    val period: Int?,
    @SerialName("subject")
    val subject: String?,
    @SerialName("time")
    val time: String?,
    @SerialName("duration")
    val duration: String?,
    @SerialName("isCurrentPeriod")
    val isCurrentPeriod: Boolean?,
)


fun NetworkTeacherTimetable.toTeacherTimetable() = TeacherTimetable(
    data = data?.map { it.toTimetableData() },
    displayToday = displayToday,
)

private fun NetworkTimetableData.toTimetableData() = TimetableData(
    day = day,
    dayNo = dayNo,
    timeTable = timeTable?.map { it.toTimetableData() }
)

private fun NetworkTimeTable.toTimetableData() = Timetable(
    className = className,
    period = period,
    subject = subject,
    time = time,
    duration = duration,
    isCurrentPeriod = isCurrentPeriod,
)





