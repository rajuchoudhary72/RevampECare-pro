package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Classe
import com.app.ecarepro.model.Teacher

data class NetworkTimeTableViewer(
    val classes: List<Classe>,
    val errorCode: Int,
    val message: String,
    val status: String,
    val teachers: List<Teacher>
)