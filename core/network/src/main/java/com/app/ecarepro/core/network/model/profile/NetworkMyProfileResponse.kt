package com.app.ecarepro.core.network.model.profile

import com.app.ecarepro.core.domain.model.profile.MyProfileData
import com.app.ecarepro.core.domain.model.profile.MyStudentProfile
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMyProfileResponse(
    @SerialName("errorCode") override val errorCode: Int = 0,
    @SerialName("message") override val message: String = "",
    @SerialName("status") override val status: String = "",
    @SerialName("canEditProfile") val canEditProfile: Boolean? = null,
    @SerialName("profile") val profile: NetworkProfileData? = null,
) : NetworkResponse

@Serializable
data class NetworkProfileData(
    // Basic
    @SerialName("name") val name: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("fName") val fName: String? = null,
    @SerialName("mName") val mName: String? = null,
    @SerialName("lName") val lName: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("dob") val dob: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("coverImg") val coverImg: String? = null,
    // Role
    @SerialName("roleName") val roleName: String? = null,
    @SerialName("designation") val designation: String? = null,
    // Family
    @SerialName("doj") val doj: String? = null,
    @SerialName("maritalStatus") val maritalStatus: String? = null,
    @SerialName("doAnniversary") val doAnniversary: String? = null,
    @SerialName("fatherHusbandName") val fatherHusbandName: String? = null,
    @SerialName("fatherHusbandMob") val fatherHusbandMob: String? = null,
    @SerialName("motherName") val motherName: String? = null,
    @SerialName("isSpouseName") val isSpouseName: Boolean? = null,
    // Contact
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("alternateMobile") val alternateMobile: String? = null,
    @SerialName("emergencyContactNo") val emergencyContactNo: String? = null,
    @SerialName("contactMobile") val contactMobile: String? = null,
    @SerialName("emailID") val emailID: String? = null,
    @SerialName("alternateEmailID") val alternateEmailID: String? = null,
    // Address
    @SerialName("address") val address: String? = null,
    @SerialName("p_Address") val permanentAddress: String? = null,
    // Religion & Identity
    @SerialName("religion") val religion: String? = null,
    @SerialName("nationality") val nationality: String? = null,
    @SerialName("bloodGroup") val bloodGroup: String? = null,
    // Professional
    @SerialName("qualification") val qualification: String? = null,
    // IDs
    @SerialName("aadharCardNo") val aadharCardNo: String? = null,
    @SerialName("paN_Number") val panNumber: String? = null,
    @SerialName("cbseid") val cbseid: String? = null,
    @SerialName("uaN_Number") val uanNumber: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("stateCode") val stateCode: String? = null,
    @SerialName("aadhaarNumber") val aadhaarNumber: String? = null,
    @SerialName("peN_Number") val penNumber: String? = null,
    @SerialName("apaaR_ID") val apaarID: String? = null,
    @SerialName("srN_UMRN_SATSNumber") val satNumber: String? = null,
    @SerialName("bankAccountNumber") val bankAccountNumber: String? = null,
    // Student-specific
    @SerialName("className") val className: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("fatherAadhaarNumber") val fatherAadhaarNumber: String? = null,
    @SerialName("motherAadhaarNumber") val motherAadhaarNumber: String? = null,
    @SerialName("admissionDate") val admissionDate: String? = null,
    @SerialName("club") val club: String? = null,
    @SerialName("house") val house: String? = null,
    // Permissions
    @SerialName("canChangeCoverImg") val canChangeCoverImg: Boolean? = null,
    @SerialName("canChangeProfileImg") val canChangeProfileImg: Boolean? = null,
    // Nested
    @SerialName("userImgReq") val userImgReq: NetworkUserImgReq? = null,
    @SerialName("studentProfile") val studentProfile: NetworkStudentProfile? = null,
    // Dropdown lists (only populated when edit=true)
    @SerialName("bloodGroupLST") val bloodGroupLST: List<NetworkDropdownItem>? = null,
    @SerialName("relegionLST") val relegionLST: List<NetworkDropdownItem>? = null,
    @SerialName("nationalityLST") val nationalityLST: List<NetworkNationalityItem>? = null,
    @SerialName("titles") val titles: List<NetworkTitleItem>? = null,
)

@Serializable
data class NetworkUserImgReq(
    @SerialName("coverImg") val coverImg: Int? = null,
    @SerialName("profileImg") val profileImg: Int? = null,
    @SerialName("childProfileImg") val childProfileImg: Int? = null,
    @SerialName("showProfilePopup") val showProfilePopup: Boolean? = null,
    @SerialName("showCoverPopup") val showCoverPopup: Boolean? = null,
    @SerialName("profilePopup") val profilePopup: NetworkImagePopup? = null,
    @SerialName("coverImgPopup") val coverImgPopup: NetworkImagePopup? = null,
)

@Serializable
data class NetworkImagePopup(
    @SerialName("message") val message: String? = null,
    @SerialName("rejectionReason") val rejectionReason: String? = null,
)

@Serializable
data class NetworkDropdownItem(
    @SerialName("id") val id: Int? = null,
    @SerialName("isSelected") val isSelected: Boolean? = null,
    @SerialName("groupName") val groupName: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("profession") val profession: String? = null,
    @SerialName("relegion") val relegion: String? = null,
    @SerialName("status") val status: String? = null,
)

@Serializable
data class NetworkTitleItem(
    @SerialName("value") val value: Int? = null,
    @SerialName("text") val text: String? = null,
)

@Serializable
data class NetworkNationalityItem(
    @SerialName("id") val id: Int? = null,
    @SerialName("nationality") val nationality: String? = null,
)

fun NetworkProfileData.toDomainModel(userType: Int, canEditProfile: Boolean): MyProfileData =
    MyProfileData(
        name = name.orEmpty(),
        username = username.orEmpty(),
        designation = designation.orEmpty(),
        roleName = roleName.orEmpty(),
        photo = photo,
        coverImg = coverImg,
        fName = fName.orEmpty(),
        mName = mName.orEmpty(),
        lName = lName.orEmpty(),
        gender = gender.orEmpty(),
        dob = dob.orEmpty(),
        doj = doj.orEmpty(),
        maritalStatus = maritalStatus.orEmpty(),
        doAnniversary = doAnniversary.orEmpty(),
        fatherHusbandName = fatherHusbandName.orEmpty(),
        fatherHusbandMob = fatherHusbandMob.orEmpty(),
        religion = religion.orEmpty(),
        nationality = nationality.orEmpty(),
        bloodGroup = bloodGroup.orEmpty(),
        mobile = mobile.orEmpty(),
        alternateMobile = alternateMobile.orEmpty(),
        emergencyContactNo = emergencyContactNo.orEmpty(),
        emailID = emailID.orEmpty(),
        alternateEmailID = alternateEmailID.orEmpty(),
        address = address.orEmpty(),
        permanentAddress = permanentAddress.orEmpty(),
        qualification = qualification.orEmpty(),
        aadhar = aadharCardNo.orEmpty(),
        pan = panNumber.orEmpty(),
        cbseId = cbseid.orEmpty(),
        uan = uanNumber.orEmpty(),
        nationalCode = nationalCode.orEmpty(),
        penNumber = penNumber.orEmpty(),
        apaarId = apaarID.orEmpty(),
        satNumber = satNumber.orEmpty(),
        canEditProfile = canEditProfile,
        canChangeProfileImg = canChangeProfileImg ?: false,
        canChangeCoverImg = canChangeCoverImg ?: false,
        userType = userType,
        studentProfile = studentProfile?.toDomainModel(),
    )
