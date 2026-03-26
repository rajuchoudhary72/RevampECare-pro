package com.app.ecarepro.core.network.model.academic

import com.app.ecarepro.core.domain.model.Assignment
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi
data class NetworkTeacherAssignment(
    @SerialName("assignments")
    val assignments: List<NetworkAssignment>?,
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("isReportView")
    val isReportView: Boolean?,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
) : NetworkResponse

@Serializable
@InternalSerializationApi
data class NetworkAssignment(
    @SerialName("asgDate")
    val asgDate: String?,
    @SerialName("asgFile")
    val asgFile: String?,
    @SerialName("asgFiles")
    val asgFiles: List<String?>?,
    @SerialName("asgID")
    val asgID: Int?,
    @SerialName("assignmentBy")
    val assignmentBy: String?,
    @SerialName("class")
    val classX: String?,
    @SerialName("hasAttachment")
    val hasAttachment: Boolean?,
    @SerialName("id")
    val id: String,
    @SerialName("isActive")
    val isActive: Boolean?,
    @SerialName("isMine")
    val isMine: Boolean?,
    @SerialName("lateSubmission")
    val lateSubmission: Boolean?,
    @SerialName("stIDs")
    val stIDs: String?,
    @SerialName("subject")
    val subject: String?,
    @SerialName("submitDate")
    val submitDate: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("updateBy")
    val updateBy: String?,
    @SerialName("uploadedOn")
    val uploadedOn: String?,
    @SerialName("userID")
    val userID: Int?,
    @SerialName("userType")
    val userType: Int?,
    @SerialName("totalSubmitted")
    val totalSubmitted: Int?,
    @SerialName("totalStudents")
    val totalStudents: Int?,
)


fun NetworkAssignment.toDomainModel() = Assignment(
    asgDate = asgDate,
    asgFile = asgFile,
    asgFiles = asgFiles,
    asgID = asgID,
    assignmentBy = assignmentBy,
    classX = classX,
    hasAttachment = hasAttachment?:false,
    id = id,
    isActive = isActive,
    isMine = isMine,
    lateSubmission = lateSubmission,
    stIDs = stIDs,
    subject = subject,
    submitDate = submitDate,
    title = title,
    updateBy = updateBy,
    uploadedOn = uploadedOn,
    userID = userID,
    userType = userType,
    totalSubmitted = totalSubmitted,
    totalStudents = totalStudents
)