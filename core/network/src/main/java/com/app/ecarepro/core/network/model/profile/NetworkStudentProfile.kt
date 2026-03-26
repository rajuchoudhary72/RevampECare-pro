package com.app.ecarepro.core.network.model.profile

import com.app.ecarepro.core.domain.model.profile.MyStudentProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkStudentProfile(
    @SerialName("name") val name: String? = null,
    @SerialName("admissionNo") val admissionNo: String? = null,
    @SerialName("rollNo") val rollNo: String? = null,
    @SerialName("dob") val dob: String? = null,
    @SerialName("className") val className: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("bloodGroup") val bloodGroup: String? = null,
    @SerialName("religion") val religion: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("city") val city: String? = null,
    @SerialName("state") val state: String? = null,
    @SerialName("contactMobile") val contactMobile: String? = null,
    @SerialName("contactEmailID") val contactEmailID: String? = null,
    @SerialName("studentEmail") val studentEmail: String? = null,
    @SerialName("parentStaus") val parentStatus: String? = null,
    @SerialName("aadhaarNumber") val aadhaarNumber: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("fatherMob_1") val fatherMob1: String? = null,
    @SerialName("fatherEmail_1") val fatherEmail1: String? = null,
    @SerialName("fatherAadhaarNumber") val fatherAadhaarNumber: String? = null,
    @SerialName("motherName") val motherName: String? = null,
    @SerialName("motherMob_1") val motherMob1: String? = null,
    @SerialName("motherEmail_1") val motherEmail1: String? = null,
    @SerialName("motherAadhaarNumber") val motherAadhaarNumber: String? = null,
)

fun NetworkStudentProfile.toDomainModel(): MyStudentProfile =
    MyStudentProfile(
        name = name.orEmpty(),
        className = className.orEmpty(),
        admissionNo = admissionNo.orEmpty(),
        rollNo = rollNo.orEmpty(),
        dob = dob.orEmpty(),
        photo = photo,
        bloodGroup = bloodGroup.orEmpty(),
        religion = religion.orEmpty(),
        address = address.orEmpty(),
        aadhaarNumber = aadhaarNumber.orEmpty(),
        fatherName = fatherName.orEmpty(),
        fatherMob = fatherMob1.orEmpty(),
        fatherEmail = fatherEmail1.orEmpty(),
        fatherAadhaar = fatherAadhaarNumber.orEmpty(),
        motherName = motherName.orEmpty(),
        motherMob = motherMob1.orEmpty(),
        motherEmail = motherEmail1.orEmpty(),
        motherAadhaar = motherAadhaarNumber.orEmpty(),
        contactMobile = contactMobile.orEmpty(),
        contactEmail = contactEmailID.orEmpty(),
    )
