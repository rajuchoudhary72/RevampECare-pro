package com.app.ecarepro.model

data class Thoughts (
    val author: String,
    val isILike: Int,
    val isVerified: Boolean,
    val likes: Int,
    val photo: String,
    val quotation: String,
    val thID: Int,
    val updatedBy: String,
    val userID: Int,
    val userType: Int
)