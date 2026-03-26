package com.app.ecarepro.core.network.model.admin

import com.app.ecarepro.core.domain.model.Syllabus
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGetSyllabuses(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("syllabuses")
    val syllabuses: List<NetworkSyllabus>?,
) : NetworkResponse

@Serializable
data class NetworkSyllabus(
    @SerialName("browsedFile")
    val browsedFile: String?,
    @SerialName("classID")
    val classID: Int?,
    @SerialName("classIDs")
    val classIDs: String?,
    @SerialName("classSTD")
    val classSTD: String,
    @SerialName("fileName")
    val fileName: String?,
    @SerialName("filePath")
    val filePath: String?,
    @SerialName("fileSize")
    val fileSize: String?,
    @SerialName("id")
    val id: String,
    @SerialName("sections")
    val sections: String?,
    @SerialName("subID")
    val subID: Int?,
    @SerialName("subject")
    val subject: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("updatedOn")
    val updatedOn: String?,
)

fun NetworkSyllabus.toDomainModel() = Syllabus(
    browsedFile = browsedFile,
    classID = classID,
    classIDs = classIDs,
    classSTD = classSTD,
    fileName = fileName,
    filePath = filePath,
    fileSize = fileSize,
    id = id,
    sections = sections,
    subID = subID,
    subject = subject,
    title = title,
    updatedOn = updatedOn
)
