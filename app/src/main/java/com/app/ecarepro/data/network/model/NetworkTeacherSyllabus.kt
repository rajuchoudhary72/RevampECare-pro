package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Syllabuse

data class NetworkTeacherSyllabus(
    val errorCode: Int,
    val message: String,
    val status: String,
    val syllabuses: List<Syllabuse>
)