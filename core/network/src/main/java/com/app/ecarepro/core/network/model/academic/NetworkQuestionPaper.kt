package com.app.ecarepro.core.network.model.academic

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class NetworkQuestionPaperResponse(
    @SerialName("errorCode") val errorCode: Int,
    @SerialName("status") val status: String?,
    @SerialName("message") val message: String?,
    @SerialName("academicYear") val academicYear: List<NetworkQPAcademicYear>?,
    @SerialName("qP_List") val qPList: List<NetworkSubjectPapers>?,
)

@InternalSerializationApi
@Serializable
data class NetworkQPAcademicYear(
    @SerialName("yrID") val yrID: Int,
    @SerialName("session") val session: String,
    @SerialName("startDate") val startDate: String?,
    @SerialName("endDate") val endDate: String?,
    @SerialName("isCur") val isCur: Boolean?,
)

@InternalSerializationApi
@Serializable
data class NetworkSubjectPapers(
    @SerialName("subID") val subID: Int?,
    @SerialName("subjectName") val subjectName: String?,
    @SerialName("questionPapers") val questionPapers: List<NetworkQuestionPaperItem>?,
)

@InternalSerializationApi
@Serializable
data class NetworkQuestionPaperItem(
    @SerialName("examName") val examName: String?,
    @SerialName("file") val file: String?,
    @SerialName("fileSize") val fileSize: String?,
    @SerialName("updatedOn") val updatedOn: String?,
)

@InternalSerializationApi
fun NetworkQuestionPaperResponse.toDomainModel() =
    com.app.ecarepro.core.domain.model.QuestionPaperResponse(
        errorCode = errorCode,
        status = status,
        message = message,
        academicYear = academicYear?.map { it.toDomainModel() },
        qPList = qPList?.map { it.toDomainModel() },
    )

@InternalSerializationApi
fun NetworkQPAcademicYear.toDomainModel() =
    com.app.ecarepro.core.domain.model.QPAcademicYear(
        yrID = yrID,
        session = session,
        startDate = startDate,
        endDate = endDate,
        isCur = isCur,
    )

@InternalSerializationApi
fun NetworkSubjectPapers.toDomainModel() =
    com.app.ecarepro.core.domain.model.SubjectPapers(
        subID = subID,
        subjectName = subjectName,
        questionPapers = questionPapers?.map { it.toDomainModel() },
    )

@InternalSerializationApi
fun NetworkQuestionPaperItem.toDomainModel() =
    com.app.ecarepro.core.domain.model.QuestionPaperItem(
        examName = examName,
        file = file,
        fileSize = fileSize,
        updatedOn = updatedOn,
    )
