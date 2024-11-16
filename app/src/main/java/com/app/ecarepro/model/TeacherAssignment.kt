package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TeacherAssignment(
    val asgDate: String?,
    val asgFile: String?,
    val asgFiles: List<String>? ,
    val asgID: Int?,
    val `class`: String?,
    val hasAttachment: Boolean?,
    val id: String?,
    val isActive: Boolean?,
    val isLateSubmitted: Boolean?,
    val lateSubmission: Boolean?,
    val subject: String?,
    val submitDate: String?,
    val title: String?,
    val updateBy: String?,
    val uploadedOn: String?,
    val userID: Int?,
    val userType: Int?
) : Parcelable