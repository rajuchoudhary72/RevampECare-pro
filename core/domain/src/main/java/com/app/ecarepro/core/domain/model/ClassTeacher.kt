package com.app.ecarepro.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ClassTeacher(
    val id: Int,
    val name: String,
    val photo: String?,
    val designation: String,
    val className: String,
)
