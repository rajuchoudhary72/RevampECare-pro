package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.NoticeData

data class NetworkNoticDetails(
     val errorCode: Int ,
    val message: String ,
    val notice: NoticeData ,
    val status: String
)


