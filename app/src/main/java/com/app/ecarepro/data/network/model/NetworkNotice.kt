package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Notice

data class NetworkNotice(
    val errorCode: Int?,
    val message: String?,
    val noticeList: List<Notice>,
    val status: String?,
    val totalNotice: Int?,
    val unreadNotice: Int?
)

data class NoticeItem(
    val detail: String?,
    val filePath: Any?,
    val fileSize: Any?,
    val hasAttachment: Boolean?,
    val heading: String?,
    val id: String?,
    val isNew: Boolean?,
    val isRead: Boolean?,
    val noticeDate: String?,
    val ntID: Int?,
    val updatedOn: String?
)

fun NoticeItem.asExternalModel() = Notice(
    detail,
    filePath,
    fileSize,
    hasAttachment,
    heading,
    id,
    isNew,
    isRead,
    noticeDate,
    ntID,
    updatedOn
)





