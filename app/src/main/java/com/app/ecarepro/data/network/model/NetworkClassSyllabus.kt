package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.SyllabusLST

data class NetworkClassSyllabus(
    val errorCode: Int,
    val message: String,
    val status: String,
    val syllabusLST: List<SyllabusLST>
)