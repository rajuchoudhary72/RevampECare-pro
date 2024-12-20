package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.StudentListMarkAtt

data class NetworkStudentListToMarkAtt(
    val canEdit: Boolean,
    val classID: Int,
    val errorCode: Int,
    val hasMarked: Boolean,
    val hasSMSSent: Boolean,
    val isLateEnable: Boolean,
    val isSubAttendance: Boolean,
    val message: String,
    val msgAlertEnable: Boolean,
    val pendingLeave: Int,
    val smS_Temp: String,
    val smsAlertEnable: Boolean,
    val smsType: Int,
    val status: String,
    val freezingTime: String?,
    val studentList: List<StudentListMarkAtt>,
    val templateID: String?
)