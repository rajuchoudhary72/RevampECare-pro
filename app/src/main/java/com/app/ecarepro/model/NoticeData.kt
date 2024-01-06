package com.app.ecarepro.model

data class NoticeData(
    val Detail: String,
    val FilePath: String,
    val FileSize: Double,
    val Heading: String,
    val ID: Any,
    val NoticeDate: String,
    val NtID: Int,
    val UpdatedOn: String,
    val hasAttachment: Int,
    val isNew: Int,
    val isRead: Int
)