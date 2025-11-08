package com.app.ecarepro.core.domain.model

import javax.annotation.concurrent.Immutable

@Immutable
data class QuestionnaireResponse(
    val errorCode: Int,
    val status: String,
    val message: String,
    val total: Int,
    val questions: List<Question>
)

@Immutable
data class Question(
    val qid: Int,
    val qType: Int,
    val que: String,
    val queImg: String?,
    val updatedBy: String,
    val updatedOn: String,
    val photo: String,
    val likes: Int,
    val isILike: Boolean,
    val totalAnswer: Int,
    val isAnswered: Boolean,
    val userID: Int,
    val userType: Int,
    val isVerified: Boolean,
    val status: String?,
    val isSelected: Boolean
)

@Immutable
data class AnswerListResponse(
    val errorCode: Int,
    val status: String,
    val message: String,
    val qid: Int,
    val question: Question,
    val answers: List<Answer>
)

@Immutable
data class Answer(
    val anID: Int,
    val answer: String,
    val answeredBy: String,
    val photo: String,
    val answeredOn: String,
    val userID: Int,
    val userType: Int,
    val isMine: Boolean
)

@Immutable
data class PostAnswerResponse(
    val errorCode: Int,
    val status: String,
    val message: String
)

@Immutable
data class AddQuestionRequest(
    val question: String,
    val attachment: AttachmentData
)

@Immutable
data class AttachmentData(
    val attachment: String,  // Base64 encoded image
    val fileExt: String,      // File extension like ".png", ".jpg"
    val fileURL: String       // Usually empty for new uploads
)

@Immutable
data class AddQuestionResponse(
    val errorCode: Int,
    val status: String,
    val message: String
)
