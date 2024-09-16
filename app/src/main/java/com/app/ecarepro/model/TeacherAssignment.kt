package com.app.ecarepro.model

import java.io.Serializable


data class TeacherAssignment(
    val asgDate: String,
    val asgFile: String,
    val asgID: Int,
    val `class`: String,
    val hasAttachment: Boolean,
    val id: String,
    val isActive: Boolean,
    val isLateSubmitted: Boolean,
    val lateSubmission: Boolean,
    val subject: String,
    val submitDate: String,
    val title: String,
    val updateBy: Any,
    val uploadedOn: String,
    val userID: Int,
    val userType: Int
): Serializable