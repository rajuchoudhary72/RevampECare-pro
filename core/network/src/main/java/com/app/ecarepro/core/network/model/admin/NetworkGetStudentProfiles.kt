package com.app.ecarepro.core.network.model.admin

import com.app.ecarepro.core.domain.model.StudentProfile
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGetStudentProfiles(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("students")
    val students: List<NetworkStudentProfile>? = null,
) : NetworkResponse

@Serializable
data class NetworkStudentProfile(
    @SerialName("stID")
    val stID: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("class")
    val className: String? = null,
    @SerialName("rollNumber")
    val rollNumber: String? = null,
    @SerialName("admissionNumber")
    val admissionNumber: String? = null,
    @SerialName("dob")
    val dob: String? = null,
    @SerialName("fatherName")
    val fatherName: String? = null,
    @SerialName("motherName")
    val motherName: String? = null,
    @SerialName("contactPerson")
    val contactPerson: String? = null,
    @SerialName("contactMob")
    val contactMob: String? = null,
    @SerialName("photo")
    val photo: String? = null,
    @SerialName("houseID")
    val houseID: Int? = null,
    @SerialName("houseName")
    val houseName: String? = null,
    @SerialName("clubID")
    val clubID: Int? = null,
    @SerialName("clubName")
    val clubName: String? = null,
    @SerialName("fatherPhoto")
    val fatherPhoto: String? = null,
    @SerialName("motherPhoto")
    val motherPhoto: String? = null,
    @SerialName("escortPhoto")
    val escortPhoto: String? = null,
    @SerialName("isSelected")
    val isSelected: Boolean? = null
)

fun NetworkStudentProfile.toDomainModel(): StudentProfile {
    val fullClass = className ?: ""
    val classParts = fullClass.split("-")
    val baseClassName = classParts.firstOrNull() ?: fullClass
    val sectionPart = if (classParts.size > 1) {
        classParts.drop(1).joinToString("-")
    } else null

    // Handle NoImage placeholder and invalid URLs
    val photoUrl = photo?.takeIf {
        !it.contains("NoImage", ignoreCase = true) &&
        it.isNotEmpty() &&
        !it.endsWith("/")
    }

    return StudentProfile(
        id = stID?.toString() ?: "",
        stID = stID,
        name = name,
        gender = gender,
        classSTD = baseClassName,
        classID = null,
        rollNumber = rollNumber,
        section = sectionPart,
        admissionNumber = admissionNumber,
        photo = photoUrl,
        dob = dob,
        fatherName = fatherName,
        motherName = motherName,
        contactPerson = contactPerson?.takeIf { it.isNotEmpty() },
        mobileNumber = contactMob?.takeIf { it.isNotEmpty() },
        emailID = null,
        address = null,
        updatedOn = null
    )
}
