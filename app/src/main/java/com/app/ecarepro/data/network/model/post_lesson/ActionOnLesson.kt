package com.app.ecarepro.data.network.model.post_lesson

data class ActionOnLesson(
    val action: Int,
    val lPlnID: Int,
    val rejectionComments: String
)