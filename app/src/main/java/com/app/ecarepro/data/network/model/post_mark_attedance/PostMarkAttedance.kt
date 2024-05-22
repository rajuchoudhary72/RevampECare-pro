package com.app.ecarepro.data.network.model.post_mark_attedance

data class PostMarkAttedance(
    val attDate: String,
    val classID: Int,
    val mode: Int,
    val studentAtt: List<StudentAtt>,
    val subID: Int
)