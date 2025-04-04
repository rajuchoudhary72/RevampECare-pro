package com.app.ecarepro.model

import com.app.ecarepro.ui.gallery.kid_corner.model.Album

data class NetworkKidCornerModel(
    val academicYears: List<AcademicYear>,
    val albums: List<Album>,
    val errorCode: Int,
    val message: String,
    val status: String
)