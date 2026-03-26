package com.app.ecarepro.core.network.model.survey

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSurveyPostAnswerRequest(
    @SerialName("id") val id: String,
    @SerialName("questions") val questions: List<NetworkSurveyQuestionPayload>,
)

@Serializable
data class NetworkSurveyQuestionPayload(
    @SerialName("queID") val queID: Int,
    @SerialName("question") val question: String,
    @SerialName("isMultiSelect") val isMultiSelect: Boolean,
    @SerialName("isAnsMandatory") val isAnsMandatory: Boolean,
    @SerialName("response") val response: Int,
    @SerialName("answer") val answer: String = "",
    @SerialName("options") val options: List<NetworkSurveyOptionPayload>,
)

@Serializable
data class NetworkSurveyOptionPayload(
    @SerialName("optID") val optID: Int,
    @SerialName("option") val option: String,
    @SerialName("isSelected") val isSelected: Boolean,
    @SerialName("response") val response: Int,
)
