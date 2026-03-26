package com.app.ecarepro.core.domain.model

data class AssignmentSubmissionReport(
    val hasAttachment: Boolean?,
    val notSubmitted: Int?,
    val offlineSubmitted: Int?,
    val studentList: List<AssignmentStudent>?,
    val submittedBy: Int?,
    val totalStudent: Int?,
)

data class AssignmentStudent(
    val asgData: String?,
    val asgFile: String?,
    val asgSubID: Int?,
    val isLateSubmitted: Boolean?,
    val isOfflineSubmitted: Boolean?,
    val remark: String?,
    val rollNumber: String?,
    val stID: Int,
    val studentName: String?,
    val submittedOn: String?,
)

