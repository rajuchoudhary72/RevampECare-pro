package com.app.ecarepro.core.network.model.announcement

import com.app.ecarepro.core.domain.model.AcademicYear
import com.app.ecarepro.core.domain.model.Circular
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGetCirculars(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("totalCirculer") val totalCirculer: Int? = null,
    @SerialName("unreadCirculer") val unreadCirculer: Int? = null,
    @SerialName("enableCreate") val enableCreate: Boolean? = null,
    @SerialName("circularList") val circularList: List<NetworkCircular>?,
    @SerialName("academicYears") val academicYears: List<NetworkAcademicYear>?,
) : NetworkResponse

@Serializable
data class NetworkGetCircularDetail(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("circuler") val circuler: NetworkCircular?,
) : NetworkResponse

@Serializable
data class NetworkCircular(
    @SerialName("cirID") val cirID: Int,
    @SerialName("title") val title: String,
    @SerialName("message") val message: String?,
    @SerialName("cirDate") val cirDate: String?,
    @SerialName("updatedOn") val updatedOn: String?,
    @SerialName("isNew") val isNew: Boolean = false,
    @SerialName("isRead") val isRead: Boolean = false,
    @SerialName("hasAttachment") val hasAttachment: Boolean = false,
    @SerialName("filePath") val filePath: String?,
    @SerialName("fileSize") val fileSize: String?,
    @SerialName("isEditable") val isEditable: Boolean? = null,
    @SerialName("postedBy") val postedBy: Int? = null,
    @SerialName("userDTL") val userDTL: NetworkCircularUserDTL? = null,
    @SerialName("id") val id: String?,
    @SerialName("mustRead") val mustRead: Boolean = false,
    @SerialName("readBy") val readBy: Int? = null,
    @SerialName("sentTo") val sentTo: String? = null,
    @SerialName("classes") val classes: String? = null,
    @SerialName("status") val status: String? = null,
)

@Serializable
data class NetworkCircularUserDTL(
    @SerialName("name") val name: String?,
    @SerialName("photo") val photo: String?,
    @SerialName("userID") val userID: Int? = null,
    @SerialName("userType") val userType: Int? = null,
    @SerialName("otherInfo") val otherInfo: String?,
    @SerialName("childName") val childName: String?,
)

@Serializable
data class NetworkAcademicYear(
    @SerialName("yrID") val yrID: Int,
    @SerialName("session") val session: String,
    @SerialName("isCur") val isCur: Boolean,
    @SerialName("startDate") val startDate: String?,
    @SerialName("endDate") val endDate: String?,
)

fun NetworkCircular.toDomainModel() = Circular(
    cirID = cirID,
    title = title,
    message = message,
    cirDate = cirDate,
    updatedOn = updatedOn,
    isNew = isNew,
    isRead = isRead,
    hasAttachment = hasAttachment,
    filePath = filePath,
    fileSize = fileSize,
    mustRead = mustRead,
    id = id ?: cirID.toString(),
    postedByName = userDTL?.name,
    postedByPhoto = userDTL?.photo,
    postedByRole = userDTL?.otherInfo,
)

fun NetworkAcademicYear.toDomainModel() = AcademicYear(
    yrID = yrID,
    session = session,
    isCur = isCur,
    startDate = startDate ?: "",
    endDate = endDate ?: "",
)
