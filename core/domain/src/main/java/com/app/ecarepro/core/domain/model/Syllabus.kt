package com.app.ecarepro.core.domain.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class Syllabus(
    val browsedFile: String?,
    val classID: Int?,
    val classIDs: String?,
    val classSTD: String,
    val fileName: String?,
    val filePath: String?,
    val fileSize: String?,
    val id: String,
    val sections: String?,
    val subID: Int?,
    val subject: String?,
    val title: String?,
    val updatedOn: String?,
)