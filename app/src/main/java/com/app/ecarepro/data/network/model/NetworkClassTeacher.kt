package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Teacher

data class NetworkClassTeacher(
    val errorCode: Int,
    val message: String,
    val status: String,
    val teachers: List<Teacher>?
)