package com.app.ecarepro.core.network.model.announcement

import com.app.ecarepro.core.domain.model.Notice
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGetNotices(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("totalNotice") val totalNotice: Int? = null,
    @SerialName("unreadNotice") val unreadNotice: Int? = null,
    @SerialName("noticeList") val noticeList: List<NetworkNotice>?,
) : NetworkResponse

@Serializable
data class NetworkGetNoticeDetail(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("notice") val notice: NetworkNotice?,
) : NetworkResponse

@Serializable
data class NetworkNotice(
    @SerialName("id") val id: String?,
    @SerialName("ntID") val ntID: Int,
    @SerialName("heading") val heading: String,
    @SerialName("detail") val detail: String?,
    @SerialName("noticeDate") val noticeDate: String?,
    @SerialName("updatedOn") val updatedOn: String?,
    @SerialName("isNew") val isNew: Boolean = false,
    @SerialName("isRead") val isRead: Boolean = false,
    @SerialName("hasAttachment") val hasAttachment: Boolean = false,
    @SerialName("filePath") val filePath: String?,
    @SerialName("fileSize") val fileSize: String?,
)

fun NetworkNotice.toDomainModel() = Notice(
    id = id ?: ntID.toString(),
    ntID = ntID,
    heading = heading,
    detail = detail,
    noticeDate = noticeDate,
    updatedOn = updatedOn,
    isNew = isNew,
    isRead = isRead,
    hasAttachment = hasAttachment,
    filePath = filePath,
    fileSize = fileSize,
)
