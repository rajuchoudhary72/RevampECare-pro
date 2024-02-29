package com.app.ecarepro.model

data class Question(
    val isAnswered: Boolean,
    val isILike: Boolean,
    val isVerified: Boolean,
    val likes: Int,
    val photo: String,
    val qType: Int,
    val qid: Int,
    val que: String,
    val queImg: Any,
    val totalAnswer: Int,
    val updatedBy: String,
    val updatedOn: String,
    val userID: Int,
    val userType: Int
)