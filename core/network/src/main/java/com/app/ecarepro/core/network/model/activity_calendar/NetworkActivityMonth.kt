package com.app.ecarepro.core.network.model.activity_calendar

import com.app.ecarepro.core.domain.model.activity_calendar.ActivityItem
import com.app.ecarepro.core.domain.model.activity_calendar.ActivityMonth
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkActivityMonth(
    @SerialName("monthNo") val monthNo: Int? = null,
    @SerialName("monthName") val monthName: String? = null,
    @SerialName("year") val year: Int? = null,
    @SerialName("isCurrent") val isCurrent: Boolean? = null,
    @SerialName("activity") val activity: List<NetworkActivityItem>? = null,
)

fun NetworkActivityMonth.toDomainModel() = ActivityMonth(
    monthNo = monthNo ?: 0,
    monthName = monthName.orEmpty(),
    year = year ?: 0,
    isCurrent = isCurrent ?: false,
    activities = activity?.map { it.toDomainModel() } ?: emptyList(),
)
