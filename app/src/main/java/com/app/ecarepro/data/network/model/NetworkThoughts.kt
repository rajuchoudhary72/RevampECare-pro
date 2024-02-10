package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Thoughts

data class NetworkThoughts(
    val errorCode: Int,
    val list: List<Thoughts>,
    val message: String,
    val status: String,
    val total: Int
)