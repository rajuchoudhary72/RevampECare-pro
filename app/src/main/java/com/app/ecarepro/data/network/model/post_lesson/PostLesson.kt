package com.app.ecarepro.data.network.model.post_lesson

data class PostLesson(
    val attachment: Attachment,
    val auditory: String,
    val classIds: String,
    val closure: String,
    val extensionTopic: String,
    val fileName: String,
    val fromDate: String,
    val introduction: String,
    val kinestheticActivity: String,
    val lPlnID: Int,
    val learningOutcomes: String,
    val objective: String,
    val otherResources: String,
    val resources: String,
    val showToStudent: Boolean,
    val subID: Int,
    val tillDate: String,
    val topic: String,
    val youtubeLinks: String
)