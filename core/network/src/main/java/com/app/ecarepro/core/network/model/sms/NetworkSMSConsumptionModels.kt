package com.app.ecarepro.core.network.model.sms

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSMSConsumptionResponse(
    @SerialName("errorCode") override val errorCode: Int = 0,
    @SerialName("message") override val message: String = "",
    @SerialName("status") override val status: String = "",
    @SerialName("dateWise") val dateWise: List<NetworkSMSConsumptionItem>? = null,
) : NetworkResponse

@Serializable
data class NetworkSMSConsumptionItem(
    @SerialName("count") val count: Int? = null,
    @SerialName("sentOn") val sentOn: String? = null,
)
