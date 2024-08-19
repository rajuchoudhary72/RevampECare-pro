package com.app.ecarepro.model

data class AssignSubmitStudent(
    val asgData: String,
    val asgFile: String,
    val asgSubID: Int,
    val isOfflineSubmitted: Boolean,
    val isLateSubmitted: Boolean,

    val rollNumber: String,
    val stID: Int,
    val studentName: String,
    val submittedOn: String
)