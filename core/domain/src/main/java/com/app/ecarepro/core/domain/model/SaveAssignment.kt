package com.app.ecarepro.core.domain.model

data class SaveAssignment(
    val id: String,
    val asgID: Int,
    val classID: Int,
    val subjectID: Int,
    val title: String,
    val asgDate: String,
    val submitDate: String,
    val data: String,
    val files: List<String>,
    val file: String,
    val isFileRemoved: Boolean,
    val isActive: Boolean,
    val lateSubMission: Boolean,
    val multipleSubMission: Boolean,
    val attachments: List<AssignmentAttachment>,
    val removedFiles: List<String>,
    val classIDStID: List<ClassIDStID>,
    val classIDs: String,
)

data class AssignmentAttachment(
    val attachment: String,
    val fileExt: String,
)

data class ClassIDStID(
    val classID: Int,
    val stIDs: String,
)

