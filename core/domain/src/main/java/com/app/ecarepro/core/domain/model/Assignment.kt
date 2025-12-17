package com.app.ecarepro.core.domain.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable


@Serializable
@InternalSerializationApi
data class Assignment(
    val asgDate: String?,
    val asgFile: String?,
    val asgFiles: List<String?>?,
    val asgID: Int?,
    val assignmentBy: String?,
    val classX: String?,
    val hasAttachment: Boolean,
    val id: String,
    val isActive: Boolean?,
    val isMine: Boolean?,
    val lateSubmission: Boolean?,
    val stIDs: String?,
    val subject: String?,
    val submitDate: String?,
    val title: String?,
    val updateBy: String?,
    val uploadedOn: String?,
    val userID: Int?,
    val userType: Int?,
    val totalSubmitted: Int?,
    val totalStudents: Int?,
)