package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Student

data class NetworkStudentList(
    val errorCode: Int,
    val message: String,
    val status: String,
    val students: ArrayList<Student>
)