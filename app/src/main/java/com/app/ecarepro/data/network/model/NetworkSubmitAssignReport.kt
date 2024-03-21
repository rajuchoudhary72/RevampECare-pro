package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AssignSubmitStudent

data class NetworkSubmitAssignReport(
    val errorCode: Int,
    val hasAttachment: Boolean,
    val message: String,
    val notSubmitted: Int,
    val offlineSubmitted: Int,
    val status: String,
    val studentList: List<AssignSubmitStudent>,
    val submittedBy: Int,
    val totalStudent: Int
)