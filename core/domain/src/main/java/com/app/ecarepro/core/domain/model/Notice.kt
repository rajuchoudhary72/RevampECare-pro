package com.app.ecarepro.core.domain.model

data class Notice(
    val id: String,
    val ntID: Int,
    val heading: String,
    val detail: String?,
    val noticeDate: String?,
    val updatedOn: String?,
    val isNew: Boolean,
    val isRead: Boolean,
    val hasAttachment: Boolean,
    val filePath: String?,
    val fileSize: String?,
)
