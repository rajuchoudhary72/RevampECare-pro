package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.LikeBy

data class NetworkWhoLike(
    val errorCode: Int,
    val likeBy: List<LikeBy>,
    val message: String,
    val status: String
)