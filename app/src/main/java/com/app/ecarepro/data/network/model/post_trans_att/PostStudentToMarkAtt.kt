package com.app.ecarepro.data.network.model.post_trans_att

data class PostStudentToMarkAtt(
    val attDate: String,
    val routeID: Int,
    val stopID: Int,
    val stuAtt: List<StuAtt>,
    val trip: Int
)