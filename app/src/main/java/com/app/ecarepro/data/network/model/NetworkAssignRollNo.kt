package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.StudentRllNo

data class NetworkAssignRollNo(
    val canAutoAssignRollNo: Boolean,
    val errorCode: Int,
    val houses: Any,
    val message: String,
    val status: String,
    val students: List<StudentRllNo>
)