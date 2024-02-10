package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Slide
import com.google.gson.annotations.SerializedName


data class NetworkWalkThrough(
    @SerializedName("errorCode")
    val errorCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("slides")
    val slides: List<NetworkSlide>,
    @SerializedName("status")
    val status: String
)

data class NetworkSlide(
    @SerializedName("heading")
    val heading: String,
    @SerializedName("imageURL")
    val imageURL: String,
    @SerializedName("text")
    val text: String
)

fun NetworkSlide.asExternalModel() = Slide(
    heading = heading,
    imageURL = imageURL,
    text = text
)