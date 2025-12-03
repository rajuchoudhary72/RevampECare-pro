package com.app.ecarepro.core.network.model.admin

import com.app.ecarepro.core.domain.model.SaveSyllabus
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi
data class NetworkSaveSyllabus(
    @SerialName("browsedFile")
    val browsedFile: NetworkBrowsedFile?,
    @SerialName("classID")
    val classID: Int,
    @SerialName("classIDs")
    val classIDs: String?,
    @SerialName("id")
    val id: String,
    @SerialName("subID")
    val subID: Int,
    @SerialName("title")
    val title: String,
    @SerialName("fileName")
    val fileName: String?,
)

@Serializable
@InternalSerializationApi
data class NetworkBrowsedFile(
    @SerialName("attachment")
    val attachment: String?,
    @SerialName("fileExt")
    val fileExt: String,
)

fun SaveSyllabus.toNetworkModel() = NetworkSaveSyllabus(
    browsedFile = NetworkBrowsedFile(
        attachment = browsedFile?.attachment,
        fileExt = browsedFile?.fileExt.orEmpty()
    ),
    classID = classID,
    classIDs = classIDs,
    id = id,
    subID = subID,
    title = title,
    fileName = fileName
)

