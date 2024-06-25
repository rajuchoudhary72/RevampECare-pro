package com.app.ecarepro.data.network.model

data class NetworkFavorites(
    val errorCode: Int,
    val list: List<FavList>,
    val message: String,
    val status: String
)