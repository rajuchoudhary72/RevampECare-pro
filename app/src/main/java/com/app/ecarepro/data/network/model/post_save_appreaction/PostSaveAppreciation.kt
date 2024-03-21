package com.app.ecarepro.data.network.model.post_save_appreaction

data class PostSaveAppreciation(
    val action: Int,
    val appreciationOn: String,
    val aprSubID: Int,
    val instance: Int,
    val remark: String,
    val rwdID: Int,
    val stID: Int
)