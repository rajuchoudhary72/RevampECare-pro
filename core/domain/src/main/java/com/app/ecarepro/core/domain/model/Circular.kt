package com.app.ecarepro.core.domain.model

data class Circular(
    val cirID: Int,
    val title: String,
    val message: String?,
    val cirDate: String?,
    val updatedOn: String?,
    val isNew: Boolean,
    val isRead: Boolean,
    val hasAttachment: Boolean,
    val filePath: String?,
    val fileSize: String?,
    val mustRead: Boolean,
    val id: String,
    val postedByName: String?,
    val postedByPhoto: String?,
    val postedByRole: String?,
)
