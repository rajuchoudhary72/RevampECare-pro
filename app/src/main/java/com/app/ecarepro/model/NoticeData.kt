package com.app.ecarepro.model

data class NoticeData(
    val detail: String,
    val filePath: String,
    val fileSize: String,
    val hasAttachment: Boolean,
    val heading: String,
    val id: Any,
    val isNew: Boolean,
    val isRead: Boolean,
    val noticeDate: String,
    val ntID: Int,
    val updatedOn: String
)