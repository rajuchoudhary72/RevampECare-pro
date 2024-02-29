package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName

data class PostAnswerPostData(
    @SerializedName("qid")
    val qid: String?,
    @SerializedName("answer")
    val answer: String?
)
