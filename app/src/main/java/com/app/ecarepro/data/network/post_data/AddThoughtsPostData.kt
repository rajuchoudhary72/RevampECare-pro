package com.app.ecarepro.data.network.post_data

import com.google.gson.annotations.SerializedName

data class AddThoughtsPostData(
    @SerializedName("quotation")
    val quotation: String?,
    @SerializedName("author")
    val author: String?
)
