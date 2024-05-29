package com.app.ecarepro.data.network.model

data class FavList (
    val fileName: String,
    val galleryType: Int,
    val id: String,
    val isFavourite: Boolean,
    val islLike: Int,
    val totalLike: Int
)