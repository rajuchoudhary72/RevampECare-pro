package com.app.ecarepro.core.network.model.survey

import com.app.ecarepro.core.domain.model.survey.SurveyOption
import com.app.ecarepro.core.domain.model.survey.SurveyQuestion
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSurveyQuestionsResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("surID") val surID: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("questions") val questions: List<NetworkSurveyQuestion>? = null,
) : NetworkResponse

@Serializable
data class NetworkSurveyQuestion(
    @SerialName("queID") val queID: Int? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("question") val question: String? = null,
    @SerialName("isMultiSelect") val isMultiSelect: Boolean? = null,
    @SerialName("isAnsMandatory") val isAnsMandatory: Boolean? = null,
    @SerialName("response") val response: Int? = null,
    @SerialName("textBoxOnly") val textBoxOnly: Boolean? = null,
    @SerialName("answer") val answer: String? = null,
    @SerialName("options") val options: List<NetworkSurveyOption>? = null,
)

@Serializable
data class NetworkSurveyOption(
    @SerialName("optID") val optID: Int? = null,
    @SerialName("option") val option: String? = null,
    @SerialName("isSelected") val isSelected: Boolean? = null,
    @SerialName("response") val response: Int? = null,
    @SerialName("resDTL") val resDTL: String? = null,
)

fun NetworkSurveyQuestion.toDomainModel() = SurveyQuestion(
    queID = queID ?: 0,
    id = id.orEmpty(),
    question = question.orEmpty(),
    isMultiSelect = isMultiSelect ?: false,
    isAnsMandatory = isAnsMandatory ?: false,
    response = response ?: 0,
    textBoxOnly = textBoxOnly ?: false,
    answer = answer,
    options = options?.map { it.toDomainModel() }.orEmpty(),
)

fun NetworkSurveyOption.toDomainModel() = SurveyOption(
    optID = optID ?: 0,
    option = option.orEmpty(),
    isSelected = isSelected ?: false,
    response = response ?: 0,
)
