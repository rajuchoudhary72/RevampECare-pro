package com.app.ecarepro.model

import com.app.ecarepro.model.photo_setting.AlbumSetting

data class Photo(
    val description: Any,
    val isFavourite: Boolean,
    val isLike: Boolean,
    val likes: Int,
    val id: String,
    val photoPath: String,
    val title: String,
 )