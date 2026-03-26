package com.app.ecarepro.core.network.model.admin

import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGetStaffProfiles(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("staffs")
    val staff: List<NetworkStaffProfile>? = null,
) : NetworkResponse

@Serializable
data class NetworkStaffProfile(
    @SerialName("sid")
    val sid: Int? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("designation")
    val designation: String? = null,
    @SerialName("staffType")
    val staffType: String? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("maritialStatus")
    val maritialStatus: String? = null,
    @SerialName("mobile")
    val mobile: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("qualification")
    val qualification: String? = null,
    @SerialName("teachersSubject")
    val teachersSubject: String? = null,
    @SerialName("photo")
    val photo: String? = null,
    @SerialName("pending")
    val pending: Int? = null,
    @SerialName("isSelected")
    val isSelected: Boolean? = null
)

fun NetworkStaffProfile.toDomainModel(): StaffProfile {
    // Handle empty or invalid photo URLs
    val photoUrl = photo?.takeIf {
        it.isNotEmpty() && !it.endsWith("/")
    }

    return StaffProfile(
        id = sid?.toString() ?: "",
        sid = sid ?: 0,
        staffId = id,
        name = name ?: "",
        designation = designation ?: "",
        staffType = staffType ?: "",
        gender = gender,
        maritalStatus = maritialStatus,
        mobile = mobile,
        email = email,
        qualification = qualification,
        teachersSubject = teachersSubject,
        photo = photoUrl
    )
}
