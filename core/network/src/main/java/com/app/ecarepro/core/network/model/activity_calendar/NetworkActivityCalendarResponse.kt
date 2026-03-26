package com.app.ecarepro.core.network.model.activity_calendar

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkActivityCalendarResponse(
    @SerialName("errorCode") override val errorCode: Int = 0,
    @SerialName("status") override val status: String = "",
    @SerialName("message") override val message: String = "",
    @SerialName("session") val session: String? = null,
    @SerialName("enableCreate") val enableCreate: Boolean? = null,
    @SerialName("activityMonth") val activityMonth: List<NetworkActivityMonth>? = null,
) : NetworkResponse
