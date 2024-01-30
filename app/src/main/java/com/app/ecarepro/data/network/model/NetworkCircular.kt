package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.Circular

data class NetworkCircular(
    val academicYears: List<AcademicYear>,
    val circularList: List<Circular>,
    val errorCode: Int,
    val message: String,
    val status: String,
    val totalCirculer: Int,
    val unreadCirculer: Int
)