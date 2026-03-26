package com.app.ecarepro.core.domain.model

data class SaveSyllabus(
    val browsedFile: BrowsedFile?,
    val classID: Int,
    val classIDs: String?,
    val id: String = "",
    val subID: Int,
    val title: String,
    val fileName: String?,
)

data class BrowsedFile(
    val attachment: String?,
    val fileExt: String,
)