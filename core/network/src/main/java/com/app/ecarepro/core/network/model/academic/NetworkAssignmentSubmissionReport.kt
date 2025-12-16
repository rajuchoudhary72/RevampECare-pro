package com.app.ecarepro.core.network.model.academic

import com.app.ecarepro.core.domain.model.AssignmentStudent
import com.app.ecarepro.core.domain.model.AssignmentSubmissionReport
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.collections.map

@Serializable
@InternalSerializationApi
data class NetworkAssignmentSubmissionReport(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("hasAttachment") val hasAttachment: Boolean?,
    @SerialName("message") override val message: String,
    @SerialName("notSubmitted") val notSubmitted: Int?,
    @SerialName("offlineSubmitted") val offlineSubmitted: Int?,
    @SerialName("status") override val status: String,
    @SerialName("studentList") val studentList: List<NetworkAssignmentStudent>?,
    @SerialName("submittedBy") val submittedBy: Int?,
    @SerialName("totalStudent") val totalStudent: Int?,
) : NetworkResponse

@Serializable
@InternalSerializationApi
data class NetworkAssignmentStudent(
    @SerialName("asgData") val asgData: String?,
    @SerialName("asgFile") val asgFile: String?,
    @SerialName("asgSubID") val asgSubID: Int?,
    @SerialName("isLateSubmitted") val isLateSubmitted: Boolean?,
    @SerialName("isOfflineSubmitted") val isOfflineSubmitted: Boolean?,
    @SerialName("remark") val remark: String?,
    @SerialName("rollNumber") val rollNumber: String?,
    @SerialName("stID") val stID: Int,
    @SerialName("studentName") val studentName: String?,
    @SerialName("submittedOn") val submittedOn: String?,
)

fun NetworkAssignmentSubmissionReport.toDomainModel() = AssignmentSubmissionReport(
    hasAttachment = hasAttachment,
    notSubmitted = notSubmitted,
    offlineSubmitted = offlineSubmitted,
    submittedBy = submittedBy,
    totalStudent = totalStudent,
    studentList = studentList?.map {
        AssignmentStudent(
            asgData = it.asgData,
            asgFile = it.asgFile,
            asgSubID = it.asgSubID,
            isLateSubmitted = it.isLateSubmitted,
            isOfflineSubmitted = it.isOfflineSubmitted,
            remark = it.remark,
            rollNumber = it.rollNumber,
            stID = it.stID,
            studentName = it.studentName,
            submittedOn = it.submittedOn
        )
    },
)

