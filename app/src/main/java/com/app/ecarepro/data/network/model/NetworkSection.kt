package com.app.ecarepro.data.network.model

data class NetworkSection(
    val errorCode: Int,
    val message: String,
    val sections: List<ClassSection>,
    val status: String
)