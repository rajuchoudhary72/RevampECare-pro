package com.app.ecarepro.core.domain.model.globalsearch

data class SearchStaff(
    val sid: Int,
    val name: String,
    val designation: String,
    val photo: String,
    val mobile: String,
    val gender: String,
    val staffType: String,
)
