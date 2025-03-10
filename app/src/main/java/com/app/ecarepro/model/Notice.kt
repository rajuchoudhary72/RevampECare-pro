package com.app.ecarepro.model

data class Notice(
    val detail: String?,
    val filePath: Any?,
    val fileSize: Any?,
    val hasAttachment: Boolean?,
    val heading: String?,
    val id: String?,
    val isNew: Boolean?,
    var isRead: Boolean?,
    val noticeDate: String?,
    val ntID: Int?,
    val updatedOn: String?
)

