package com.app.ecarepro.data.network.model.SendSpecificMsg

data class Recipient(
    val body: String,
    val receiverID: Int,
    val receiverType: Int
)