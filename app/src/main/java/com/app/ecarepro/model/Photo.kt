package com.app.ecarepro.model

data class Photo(
    val description: Any,
    val isFavourite: Boolean,
    val isLike: Boolean,
    val likes: Int,
    val id: String,
    val photoPath: String,
    val title: String
)