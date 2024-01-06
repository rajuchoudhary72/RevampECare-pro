package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.NoticeData

data class NetworkNoticDetails(
    val AWS_Path: String,
    val ErrorCode: Int,
    val Message: String,
    val Notice: Notices,
    val Status: String
)

data class Notices(
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

fun Notices.asExternalModel()= NoticeData(
    Detail, FilePath, FileSize, Heading, ID, NoticeDate, NtID, UpdatedOn, hasAttachment, isNew, isRead
)