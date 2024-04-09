package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class UploadPhotoRequest(
    @SerializedName("cover")
    val cover: String? = null,
    @SerializedName("coverExt")
    val coverExt: String? = null,
    @SerializedName("profile")
    val profile: String? = null,
    @SerializedName("profileExt")
    val profileExt: String? = null,
    @SerializedName("studentPhoto")
    val studentPhoto: String? = null,
    @SerializedName("studentPhotoExt")
    val studentPhotoExt: String? = null,
    @SerializedName("type")
    val type: Int
)