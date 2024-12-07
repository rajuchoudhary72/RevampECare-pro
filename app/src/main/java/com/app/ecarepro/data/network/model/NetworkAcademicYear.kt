package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AcademicYear

data class NetworkAcademicYear(
    val errorCode: Int,
    val message: String,
    val status: String,
    val years: List<AcademicYear>,
)