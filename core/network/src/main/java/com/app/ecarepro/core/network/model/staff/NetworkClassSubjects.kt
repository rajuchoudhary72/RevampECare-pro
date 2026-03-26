package com.app.ecarepro.core.network.model.staff

import com.app.ecarepro.core.domain.model.Subject
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class NetworkClassSubjects(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("mySubjects")
    val mySubjects: List<NetworkSubject>?,
    @SerialName("status")
    override val status: String,
) : NetworkResponse

@InternalSerializationApi
@Serializable
data class NetworkSubject(
    @SerialName("classID")
    val classID: Int?,
    @SerialName("period_Subject")
    val periodSubject: String?,
    @SerialName("shortName")
    val shortName: String?,
    @SerialName("subID")
    val subID: Int?,
    @SerialName("subjectName")
    val subjectName: String?,
)


fun NetworkSubject.toDomainModel() = Subject(
    classID = classID,
    periodSubject = periodSubject,
    shortName = shortName,
    subID = subID,
    subjectName = subjectName
)


