package com.app.ecarepro.model

data class Circular(
    val cirDate: String,
    val cirID: Int,
    val filePath: String,
    val fileSize: String,
    val hasAttachment: Boolean,
    val id: String,
    val isEditable: Boolean,
    val isNew: Boolean,
    val isRead: Boolean,
    val message: String,
    val mustRead: Boolean,
    val postedBy: Int,
    val title: String,
    val updatedOn: String,
    val userDTL: Any
)