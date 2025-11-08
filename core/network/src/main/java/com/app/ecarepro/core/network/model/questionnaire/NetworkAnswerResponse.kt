package com.app.ecarepro.core.network.model.questionnaire

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class NetworkAnswerListResponse(
    @SerialName("errorCode")
    val errorCode: Int,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("qid")
    val qid: Int,
    @SerialName("question")
    val question: NetworkQuestion,
    @SerialName("list")
    val list: List<NetworkAnswer>
)

@InternalSerializationApi
@Serializable
data class NetworkAnswer(
    @SerialName("anID")
    val anID: Int,
    @SerialName("answer")
    val answer: String,
    @SerialName("answeredBy")
    val answeredBy: String,
    @SerialName("photo")
    val photo: String,
    @SerialName("answeredOn")
    val answeredOn: String,
    @SerialName("userID")
    val userID: Int,
    @SerialName("userType")
    val userType: Int,
    @SerialName("isMine")
    val isMine: Boolean
)

@InternalSerializationApi
@Serializable
data class NetworkPostAnswerRequest(
    @SerialName("qid")
    val qid: Int,
    @SerialName("answer")
    val answer: String
)

@InternalSerializationApi
@Serializable
data class NetworkPostAnswerResponse(
    @SerialName("errorCode")
    val errorCode: Int,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String
)

@InternalSerializationApi
@Serializable
data class NetworkAddQuestionRequest(
    @SerialName("question")
    val question: String,
    @SerialName("attachment")
    val attachment: NetworkAttachmentData
)

@InternalSerializationApi
@Serializable
data class NetworkAttachmentData(
    @SerialName("attachment")
    val attachment: String,  // Base64 encoded image
    @SerialName("fileExt")
    val fileExt: String,      // File extension like ".png", ".jpg"
    @SerialName("fileURL")
    val fileURL: String       // Usually empty for new uploads
)

@InternalSerializationApi
@Serializable
data class NetworkAddQuestionResponse(
    @SerialName("errorCode")
    val errorCode: Int,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String
)
