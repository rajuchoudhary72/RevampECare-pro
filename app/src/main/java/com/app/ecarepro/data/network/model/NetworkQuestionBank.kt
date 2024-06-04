package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.QBQuestion

data class NetworkQuestionBank(
    val errorCode: Int,
    val message: String,
    val qB_Questions: List<QBQuestion>,
    val status: String
)