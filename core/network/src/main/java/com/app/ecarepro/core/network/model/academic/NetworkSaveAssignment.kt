package com.app.ecarepro.core.network.model.academic

import com.app.ecarepro.core.domain.model.SaveAssignment
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi
data class NetworkSaveAssignment(
    @SerialName("asgDate")
    val asgDate: String?,
    @SerialName("asgID")
    val asgID: Int?,
    @SerialName("attachments")
    val attachments: List<NetworkAssignmentAttachment>?,
    @SerialName("classID")
    val classID: Int?,
    @SerialName("classID_StID")
    val classIDStID: List<NetworkClassIDStID>?,
    @SerialName("classIDs")
    val classIDs: String?,
    @SerialName("data")
    val `data`: String?,
    @SerialName("file")
    val `file`: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("isActive")
    val isActive: Boolean?,
    @SerialName("isFileRemoved")
    val isFileRemoved: Boolean?,
    @SerialName("lateSubmission")
    val lateSubMission: Boolean?,
    @SerialName("multipleSubmission")
    val multipleSubMission: Boolean?,
    @SerialName("submitDate")
    val submitDate: String?,
    @SerialName("subjectID")
    val subjectID: Int?,
    @SerialName("title")
    val title: String?,
    @SerialName("files")
    val files: List<String>?,
)

@Serializable
@InternalSerializationApi
data class NetworkAssignmentAttachment(
    @SerialName("attachment")
    val attachment: String?,
    @SerialName("fileExt")
    val fileExt: String?,
)

@Serializable
@InternalSerializationApi
data class NetworkClassIDStID(
    @SerialName("classID")
    val classID: Int?,
    @SerialName("stIDs")
    val stIDs: String?,
)

fun SaveAssignment.toNetworkModel() = NetworkSaveAssignment(
    asgDate = asgDate,
    asgID = asgID,
    attachments = attachments.map {
        NetworkAssignmentAttachment(
            attachment = it.attachment,
            fileExt = it.fileExt
        )
    },
    classID = classID,
    classIDStID = classIDStID.map { NetworkClassIDStID(classID = it.classID, stIDs = it.stIDs) },
    classIDs = classIDs,
    data = data,
    file = file,
    id = id,
    isActive = isActive,
    isFileRemoved = isFileRemoved,
    lateSubMission = lateSubMission,
    multipleSubMission = multipleSubMission,
    submitDate = submitDate,
    subjectID = subjectID,
    title = title,
    files = files
)