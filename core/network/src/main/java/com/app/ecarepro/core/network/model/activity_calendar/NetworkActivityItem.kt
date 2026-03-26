package com.app.ecarepro.core.network.model.activity_calendar

import com.app.ecarepro.core.domain.model.activity_calendar.ActivityItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkActivityItem(
    @SerialName("id") val id: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("isWorking") val isWorking: Boolean? = null,
    @SerialName("fromDate") val fromDate: String? = null,
    @SerialName("tillDate") val tillDate: String? = null,
    @SerialName("duration") val duration: Int? = null,
)

fun NetworkActivityItem.toDomainModel(): ActivityItem {
    val fromDateParsed = parseDateString(fromDate.orEmpty())
    val tillDateParsed = if (!tillDate.isNullOrEmpty() && tillDate != fromDate) {
        parseDateString(tillDate)
    } else null

    return ActivityItem(
        title = title.orEmpty(),
        isWorking = isWorking ?: true,
        duration = duration ?: 1,
        displayDate = fromDateParsed.date,
        displayDayOfWeek = fromDateParsed.dayOfWeek,
        displayTillDate = tillDateParsed?.date,
        displayTillDayOfWeek = tillDateParsed?.dayOfWeek,
        isMultiDay = tillDateParsed != null,
    )
}

private data class DateParts(val date: String, val dayOfWeek: String)

private fun parseDateString(dateString: String): DateParts {
    val parts = dateString.split(" ")
    if (parts.size < 2) return DateParts(dateString, "")
    val day = parts[0]
    val monthAbbrev = parts[1].removeSuffix(",")
    val fullMonth = expandMonth(monthAbbrev)
    val dayOfWeek = if (parts.size >= 4) parts[3] else ""
    return DateParts(date = "$day $fullMonth", dayOfWeek = dayOfWeek)
}

private fun expandMonth(abbrev: String): String = when (abbrev) {
    "Jan" -> "January"; "Feb" -> "February"; "Mar" -> "March"
    "Apr" -> "April"; "May" -> "May"; "Jun" -> "June"
    "Jul" -> "July"; "Aug" -> "August"; "Sep" -> "September"
    "Oct" -> "October"; "Nov" -> "November"; "Dec" -> "December"
    else -> abbrev
}
