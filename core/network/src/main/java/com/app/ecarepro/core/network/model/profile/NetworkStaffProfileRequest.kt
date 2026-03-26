package com.app.ecarepro.core.network.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkStaffProfileRequest(
    @SerialName("fName") val fName: String = "",
    @SerialName("mName") val mName: String = "",
    @SerialName("lName") val lName: String = "",
    @SerialName("mobile") val mobile: String = "",
    @SerialName("emailID") val emailID: String = "",
    @SerialName("alternateEmailID") val alternateEmailID: String = "",
    @SerialName("alternateMobile") val alternateMobile: String = "",
    @SerialName("emergencyContactNo") val emergencyContactNo: String = "",
    @SerialName("fatherHusbandName") val fatherHusbandName: String = "",
    @SerialName("fatherHusbandMob") val fatherHusbandMob: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("p_Address") val pAddress: String = "",
    @SerialName("qualification") val qualification: String = "",
    @SerialName("aadharCardNo") val aadharCardNo: String = "",
    @SerialName("paN_Number") val paNNumber: String = "",
    @SerialName("cbseid") val cbseid: String = "",
    @SerialName("uaN_Number") val uaNNumber: String = "",
    @SerialName("nationalCode") val nationalCode: String = "",
    @SerialName("dob") val dob: String = "",
    @SerialName("doAnniversary") val doAnniversary: String = "",
    @SerialName("isMaritialStatusID") val isMaritialStatusID: Boolean = false,
    @SerialName("isBloodGroupID") val isBloodGroupID: Boolean = false,
    @SerialName("isRelegionID") val isRelegionID: Boolean = false,
    @SerialName("isNationalityID") val isNationalityID: Boolean = false,
)
