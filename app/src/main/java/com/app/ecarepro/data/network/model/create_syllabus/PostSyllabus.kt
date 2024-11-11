package com.app.ecarepro.data.network.model.create_syllabus

data class PostSyllabus(
    val browsedFile: BrowsedFile?,
    val classID: Int,
    val classIDs: String?,
    val id: String,
    val subID: Int,
    val title: String,
    val fileName: String?
)