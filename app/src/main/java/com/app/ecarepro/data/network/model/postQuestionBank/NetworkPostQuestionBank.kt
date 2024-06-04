package com.app.ecarepro.data.network.model.postQuestionBank

data class NetworkPostQuestionBank(
    val browsedImg: BrowsedImg,
    val chapter: String,
    val chapterID: Int,
    val `class`: String,
    val classSTD: Int,
    val createdOn: String,
    val filename: String,
    val hasAdded: Boolean,
    val id: String,
    val isSelected: Boolean,
    val qbid: Int,
    val queImg: String,
    val queType: String,
    val queTypeID: Int,
    val question: String,
    val subID: Int,
    val subject: String,
    val subjectInstructorID: Int,
    val weightage: String
)