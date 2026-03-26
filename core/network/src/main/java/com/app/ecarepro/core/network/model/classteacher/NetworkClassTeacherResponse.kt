package com.app.ecarepro.core.network.model.classteacher

import com.app.ecarepro.core.domain.model.ClassTeacher
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkClassTeacherResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("teachers") val teachers: List<NetworkClassTeacher>? = null,
) : NetworkResponse

@Serializable
data class NetworkClassTeacher(
    @SerialName("userID") val userID: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("class") val className: String? = null,
    @SerialName("subject") val subject: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("userType") val userType: Int? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("pending") val pending: Int? = null,
)

fun NetworkClassTeacher.toDomainModel(): ClassTeacher {
    // Filter out broken photo URLs that end without a filename
    val photoUrl = photo?.takeIf { it.isNotEmpty() && !it.endsWith("/") }
    return ClassTeacher(
        id = userID ?: 0,
        name = name ?: "",
        photo = photoUrl,
        designation = designation ?: "",
        className = className ?: "",
    )
}
