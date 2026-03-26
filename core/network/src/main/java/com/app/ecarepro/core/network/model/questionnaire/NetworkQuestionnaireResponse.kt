package com.app.ecarepro.core.network.model.questionnaire

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class NetworkQuestionnaireResponse(
    @SerialName("errorCode")
    val errorCode: Int,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("total")
    val total: Int,
    @SerialName("questions")
    val questions: List<NetworkQuestion>
)

@InternalSerializationApi
@Serializable
data class NetworkQuestion(
    @SerialName("qid")
    val qid: Int,
    @SerialName("qType")
    val qType: Int,
    @SerialName("que")
    val que: String,
    @SerialName("queImg")
    val queImg: String?,
    @SerialName("updatedBy")
    val updatedBy: String,
    @SerialName("updatedOn")
    val updatedOn: String,
    @SerialName("photo")
    val photo: String,
    @SerialName("likes")
    val likes: Int,
    @SerialName("isILike")
    val isILike: Boolean,
    @SerialName("totalAnswer")
    val totalAnswer: Int,
    @SerialName("isAnswered")
    val isAnswered: Boolean,
    @SerialName("userID")
    val userID: Int,
    @SerialName("userType")
    val userType: Int,
    @SerialName("isVerified")
    val isVerified: Boolean,
    @SerialName("status")
    val status: String?,
    @SerialName("isSelected")
    val isSelected: Boolean
)
