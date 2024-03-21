package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.MySubject

data class NetworkMySubjects(
    val errorCode: Int,
    val message: String,
    val mySubjects: List<MySubject>,
    val status: String
)