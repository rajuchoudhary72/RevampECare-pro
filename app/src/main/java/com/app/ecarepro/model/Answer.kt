package com.app.ecarepro.model

data class Answer(
    val anID: Int,
    val answer: String,
    val answeredBy: String,
    val answeredOn: String,
    val isMine: Boolean,
    val photo: String,
    val userID: Int,
    val userType: Int
)
