package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Answer
import com.app.ecarepro.model.Question

data class NetworkAnswerDetails(
    val errorCode: Int,
    val list: List<Answer>,
    val message: String,
    val qid: Int,
    val question: Question,
    val status: String
)