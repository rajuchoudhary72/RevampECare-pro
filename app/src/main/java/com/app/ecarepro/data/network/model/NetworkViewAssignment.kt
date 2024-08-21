package com.app.ecarepro.data.network.model

data class NetworkViewAssignment(
    val asgDate: String,
    val asgID: Int,
    val attachment: Any,
    val classID: Int,
    val classIDs: Any,
    val `data`: String,
    val errorCode: Int,
    val `file`: String,
    val id: String,
    val isActive: Boolean,
    val isFileRemoved: Boolean,
    val message: String,
    val multipleSubmission: Boolean,
    val lateSubmission: Boolean,
    val status: Any,
    val subjectID: Int,
    val submitDate: String,
    val hasAttachment: Boolean,
    val asgFile: String,
    val title: String,
    val stIDs: String?,

    )