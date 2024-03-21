package com.app.ecarepro.model

data class Assignment(
    val asgData: String,
    val asgDate: String,
    val asgFileName: String,
    val asgFileURL: String,
    val asgID: Int,
    val classID: Int,
    val hasAttachment: Boolean,
    val id: String,
    val isActive: Boolean,
    val isSubmissionOpened: Boolean,
    val isSubmitted: Boolean,
    val multipleSubmission: Boolean,
    val photo: String,
    val studentSubmission: Any,
    val subject: String,
    val subjectID: Int,
    val submitDate: String,
    val title: String,
    val updateBy: String,
    val uploadedOn: String,
    val userID: Int,
    val userType: Int
)