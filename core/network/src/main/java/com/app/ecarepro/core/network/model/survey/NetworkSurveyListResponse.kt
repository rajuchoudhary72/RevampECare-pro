package com.app.ecarepro.core.network.model.survey

import com.app.ecarepro.core.domain.model.survey.SurveyItem
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSurveyListResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("total") val total: Int? = null,
    @SerialName("allSurvey") val allSurvey: List<NetworkSurveyItem>? = null,
) : NetworkResponse

@Serializable
data class NetworkSurveyItem(
    @SerialName("id") val id: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("publishedOn") val publishedOn: String? = null,
    @SerialName("isOpen") val isOpen: Boolean? = null,
    @SerialName("openEndDate") val openEndDate: String? = null,
    @SerialName("isResponded") val isResponded: Boolean? = null,
    @SerialName("respondedOn") val respondedOn: String? = null,
    @SerialName("resultDeclared") val resultDeclared: Boolean? = null,
    @SerialName("postedBy") val postedBy: String? = null,
)

fun NetworkSurveyItem.toDomainModel() = SurveyItem(
    id = id.orEmpty(),
    title = title.orEmpty(),
    description = description.orEmpty(),
    publishedOn = publishedOn.orEmpty(),
    openEndDate = openEndDate.orEmpty(),
    isOpen = isOpen ?: false,
    isResponded = isResponded ?: false,
    respondedOn = respondedOn,
    resultDeclared = resultDeclared ?: false,
)
