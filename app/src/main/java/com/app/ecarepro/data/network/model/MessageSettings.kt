package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class MessageSettings(
    @SerializedName("compose")
    val compose: Boolean?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("isBoardingSchool")
    val isBoardingSchool: Boolean?,
    @SerializedName("media")
    val media: Media?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("msgWithSMS")
    val msgWithSMS: Boolean,
    @SerializedName("onlyMsg")
    val onlyMsg: Boolean,
    @SerializedName("status")
    val status: String?
) {
    fun isBothOptionVisible() = onlyMsg && msgWithSMS
}

data class Media(
    @SerializedName("browseAudio")
    val browseAudio: Boolean?,
    @SerializedName("browseImg")
    val browseImg: Boolean?,
    @SerializedName("browsePDF")
    val browsePDF: Boolean?
)