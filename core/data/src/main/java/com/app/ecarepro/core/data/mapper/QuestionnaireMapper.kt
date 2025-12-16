package com.app.ecarepro.core.data.mapper

import com.app.ecarepro.core.domain.model.AddQuestionRequest
import com.app.ecarepro.core.domain.model.AddQuestionResponse
import com.app.ecarepro.core.domain.model.Answer
import com.app.ecarepro.core.domain.model.AnswerListResponse
import com.app.ecarepro.core.domain.model.AttachmentData
import com.app.ecarepro.core.domain.model.PostAnswerResponse
import com.app.ecarepro.core.domain.model.Question
import com.app.ecarepro.core.domain.model.QuestionnaireResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkAddQuestionRequest
import com.app.ecarepro.core.network.model.questionnaire.NetworkAddQuestionResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkAnswer
import com.app.ecarepro.core.network.model.questionnaire.NetworkAnswerListResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkAttachmentData
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkQuestion
import com.app.ecarepro.core.network.model.questionnaire.NetworkQuestionnaireResponse

fun NetworkQuestionnaireResponse.toDomainModel() = QuestionnaireResponse(
    errorCode = errorCode,
    status = status,
    message = message,
    total = total,
    questions = questions.map { it.toDomainModel() }
)

fun NetworkQuestion.toDomainModel() = Question(
    qid = qid,
    qType = qType,
    que = que,
    queImg = queImg,
    updatedBy = updatedBy,
    updatedOn = updatedOn,
    photo = photo,
    likes = likes,
    isILike = isILike,
    totalAnswer = totalAnswer,
    isAnswered = isAnswered,
    userID = userID,
    userType = userType,
    isVerified = isVerified,
    status = status,
    isSelected = isSelected
)

fun NetworkAnswerListResponse.toDomainModel() = AnswerListResponse(
    errorCode = errorCode,
    status = status,
    message = message,
    qid = qid,
    question = question?.toDomainModel(),
    answers = list.map { it.toDomainModel() }
)

fun NetworkAnswer.toDomainModel() = Answer(
    anID = anID,
    answer = answer,
    answeredBy = answeredBy,
    photo = photo,
    answeredOn = answeredOn,
    userID = userID,
    userType = userType,
    isMine = isMine
)

fun NetworkPostAnswerResponse.toDomainModel() = PostAnswerResponse(
    errorCode = errorCode,
    status = status,
    message = message
)

fun AddQuestionRequest.toNetworkModel() = NetworkAddQuestionRequest(
    question = question,
    attachment = attachment.toNetworkModel()
)

fun AttachmentData.toNetworkModel() = NetworkAttachmentData(
    attachment = attachment,
    fileExt = fileExt,
    fileURL = fileURL
)

fun NetworkAddQuestionResponse.toDomainModel() = AddQuestionResponse(
    errorCode = errorCode,
    status = status,
    message = message
)
