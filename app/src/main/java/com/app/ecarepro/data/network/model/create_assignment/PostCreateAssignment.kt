package com.app.ecarepro.data.network.model.create_assignment

import com.app.ecarepro.model.ClassID_StID

data class PostCreateAssignment(
    val asgDate: String,
    val asgID: Int,

    val classID: Int,
    val classIDs: String,
    val data: String,
    val file: String,
    val id: String,
    val isActive: Boolean,
    val isFileRemoved: Boolean,
    val multipleSubmission: Boolean,

    val subjectID: Int,
    val submitDate: String,
    val title: String,
    val lateSubmission: Boolean,
    val attachments: List<com.app.ecarepro.data.network.model.Attachment>?,
    val classID_StID: List<ClassID_StID>,
    val stIDs: String?


)