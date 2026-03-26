package com.app.ecarepro.core.network.model.globalsearch

import com.app.ecarepro.core.domain.model.globalsearch.SearchStaff
import com.app.ecarepro.core.domain.model.globalsearch.SearchStudent
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSearchStudentResponse(
    @SerialName("errorCode") override val errorCode: Int = 0,
    @SerialName("status") override val status: String = "",
    @SerialName("message") override val message: String = "",
    @SerialName("students") val students: List<NetworkSearchStudent>? = null,
) : NetworkResponse

@Serializable
data class NetworkSearchStudent(
    @SerialName("stID") val stID: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("class") val studentClass: String? = null,
    @SerialName("admissionNumber") val admissionNumber: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("contactMob") val contactMob: String? = null,
    @SerialName("rollNumber") val rollNumber: String? = null,
    @SerialName("gender") val gender: String? = null,
)

fun NetworkSearchStudent.toDomainModel() = SearchStudent(
    stID = stID ?: 0,
    name = name.orEmpty(),
    studentClass = studentClass.orEmpty(),
    admissionNumber = admissionNumber.orEmpty(),
    photo = photo.orEmpty(),
    fatherName = fatherName.orEmpty(),
    contactMob = contactMob.orEmpty(),
    rollNumber = rollNumber.orEmpty(),
    gender = gender.orEmpty(),
)

@Serializable
data class NetworkSearchStaffResponse(
    @SerialName("errorCode") override val errorCode: Int = 0,
    @SerialName("status") override val status: String = "",
    @SerialName("message") override val message: String = "",
    @SerialName("staffs") val staffs: List<NetworkSearchStaff>? = null,
) : NetworkResponse

@Serializable
data class NetworkSearchStaff(
    @SerialName("sid") val sid: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("staffType") val staffType: String? = null,
)

fun NetworkSearchStaff.toDomainModel() = SearchStaff(
    sid = sid ?: 0,
    name = name.orEmpty(),
    designation = designation.orEmpty(),
    photo = photo.orEmpty(),
    mobile = mobile.orEmpty(),
    gender = gender.orEmpty(),
    staffType = staffType.orEmpty(),
)
