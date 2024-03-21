package com.app.ecarepro.data.network.model.create_assignment

import com.app.ecarepro.data.network.model.post_question.Attachment

data class PostCreateAssignment(
    val asgDate: String,
    val asgID: Int,
    val attachment: Attachment,
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
    val title: String
)