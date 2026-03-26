package com.app.ecarepro.core.network.model.birthday

import com.app.ecarepro.core.domain.model.birthday.Birthday
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkBirthdayResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("usersBirthday") val usersBirthday: List<NetworkUserBirthday>? = null,
) : NetworkResponse

@Serializable
data class NetworkUserBirthday(
    @SerialName("userID") val userID: Int? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("birthdayOf") val birthdayOf: String? = null,
    @SerialName("className") val className: String? = null,
    @SerialName("birthdayOn") val birthdayOn: String? = null,
    @SerialName("motherName") val motherName: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("userType") val userType: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("mobile") val mobile: String? = null,
)

fun NetworkUserBirthday.toDomainModel() = Birthday(
    id = userID ?: 0,
    name = name.orEmpty(),
    fatherName = fatherName?.replace("Mr.", "")?.trim()?.ifEmpty { null },
    motherName = motherName?.replace("Mrs.", "")?.trim()?.ifEmpty { null },
    birthdayOf = birthdayOf,
    className = className?.trim()?.ifEmpty { null },
    birthdayOn = birthdayOn.orEmpty(),
    photo = photo,
    userType = userType ?: 0,
    designation = designation,
    mobile = mobile?.trim()?.ifEmpty { null },
)
