package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class UserProfileDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("profile")
    val profile: Profile,
    @SerializedName("status")
    val status: String?,
    @SerializedName("canEditProfile")
    val canEditProfile: Boolean?
)

data class Profile(
    @SerializedName("aadharCardNo")
    val aadharCardNo: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("alternateEmailID")
    val alternateEmailID: String?,
    @SerializedName("alternateMobile")
    val alternateMobile: String?,
    @SerializedName("canChangeCoverImg")
    val canChangeCoverImg: Boolean?,
    @SerializedName("canChangeProfileImg")
    val canChangeProfileImg: Boolean?,
    @SerializedName("canChangeChildImg")
    val canChangeChildImg: Boolean?,
    @SerializedName("coverImg")
    val coverImg: String?,
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("doAnniversary")
    val doAnniversary: String?,
    @SerializedName("uaN_Number")
    val uaN_Number: String?,
    @SerializedName("nationalCode")
    val nationalCode : String?,
    @SerializedName("dob")
    val dob: String?,
    @SerializedName("doj")
    val doj: String?,
    @SerializedName("emailID")
    val emailID: String?,
    @SerializedName("emergencyContactNo")
    val emergencyContactNo: String?,
    @SerializedName("fName")
    val fName: String?,
    @SerializedName("fatherHusbandMob")
    val fatherHusbandMob: String?,
    @SerializedName("fatherHusbandName")
    val fatherHusbandName: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("isSpouseName")
    val isSpouseName: Boolean?,
    @SerializedName("lName")
    val lName: String?,
    @SerializedName("mName")
    val mName: String?,
    @SerializedName("maritalStatus")
    val maritalStatus: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("nationality")
    val nationality: String?,
    @SerializedName("p_Address")
    val pAddress: String?,
    @SerializedName("paN_Number")
    val paNNumber: String?,
    @SerializedName("cbseid")
    val cbseID: String?,
    @SerializedName("club")
    val club: String?,
    @SerializedName("srN_UMRN_SATSNumber")
    val srN_UMRN_SATSNumber: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("qualification")
    val qualification: String?,
    @SerializedName("religion")
    val religion: String?,
    @SerializedName("roleName")
    val roleName: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("userImgReq")
    val userImgReq: UserImgReq?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("studentProfile")
    val studentProfile: StudentProfile?,
    @SerializedName("className")
    val className: String?,
    @SerializedName("aadhaarNumber")
    val aadhaarNumber: String?,
    @SerializedName("additionalMobile")
    val additionalMobile: Any?,
    @SerializedName("admissionDate")
    val admissionDate: String?,
    @SerializedName("admissionNo")
    val admissionNo: String?,
    @SerializedName("peN_Number")
    val peN_Number: String?,
    @SerializedName("billNumber")
    val billNumber: String?,
    @SerializedName("apaaR_ID")
    val apaaR_ID: String?,
    @SerializedName("birthPlace")
    val birthPlace: String?,
    @SerializedName("bloodGroup")
    val bloodGroup: String?,
    @SerializedName("caste")
    val caste: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("classification")
    val classification: String?,
    @SerializedName("contactEmailID")
    val contactEmailID: String?,
    @SerializedName("contactMobile")
    val contactMobile: String?,
    @SerializedName("contactPerson")
    val contactPerson: String?,
    @SerializedName("diseNo")
    val diseNo: String?,
    @SerializedName("fatherAadhaarNumber")
    val fatherAadhaarNumber: String?,
    @SerializedName("fatherAnnualIncome")
    val fatherAnnualIncome: String?,
    @SerializedName("fatherDOB")
    val fatherDOB: Any?,
    @SerializedName("fatherDesignation")
    val fatherDesignation: String?,
    @SerializedName("fatherDesignationID")
    val fatherDesignationID: Any?,
    @SerializedName("fatherEmail_1")
    val fatherEmail1: String?,
    @SerializedName("fatherEmail_2")
    val fatherEmail2: String?,
    @SerializedName("fatherMob_1")
    val fatherMob1: String?,
    @SerializedName("fatherMob_2")
    val fatherMob2: String?,
    @SerializedName("fatherName")
    val fatherName: String?,
    @SerializedName("fatherOfficeAddress")
    val fatherOfficeAddress: String?,
    @SerializedName("fatherProfession")
    val fatherProfession: String?,
    @SerializedName("fatherResidentialAddress")
    val fatherResidentialAddress: String?,
    @SerializedName("house")
    val house: String?,
    @SerializedName("isBoarding")
    val isBoarding: Boolean?,
    @SerializedName("motherAadhaarNumber")
    val motherAadhaarNumber: String?,
    @SerializedName("motherAnnualIncome")
    val motherAnnualIncome: String?,
    @SerializedName("motherDOB")
    val motherDOB: Any?,
    @SerializedName("motherDesignation")
    val motherDesignation: String?,
    @SerializedName("motherEmail_1")
    val motherEmail1: String?,
    @SerializedName("motherEmail_2")
    val motherEmail2: String?,
    @SerializedName("motherMob_1")
    val motherMob1: String?,
    @SerializedName("motherMob_2")
    val motherMob2: String?,
    @SerializedName("motherName")
    val motherName: String?,
    @SerializedName("motherOfficeAddress")
    val motherOfficeAddress: String?,
    @SerializedName("motherProfession")
    val motherProfession: String?,
    @SerializedName("motherResidentialAddress")
    val motherResidentialAddress: String?,
    @SerializedName("parentAnniversaryDate")
    val parentAnniversaryDate: Any?,
    @SerializedName("parentStaus")
    val parentStaus: String?,
    @SerializedName("permanentAddress")
    val permanentAddress: String?,
    @SerializedName("permanentCity")
    val permanentCity: String?,
    @SerializedName("permanentState")
    val permanentState: String?,
    @SerializedName("previousSchoolDTL")
    val previousSchoolDTL: PreviousSchoolDTL?,
    @SerializedName("rollNo")
    val rollNo: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("transport")
    val transport: String?,
    val canEditProfile: Boolean?
)

data class UserImgReq(
    @SerializedName("childProfileImg")
    val childProfileImg: Int?,
    @SerializedName("coverImg")
    val coverImg: Int?,
    @SerializedName("profileImg")
    val profileImg: Int?
)

data class StudentProfile(
    @SerializedName("aadhaarNumber")
    val aadhaarNumber: String?,
    @SerializedName("additionalMobile")
    val additionalMobile: Any?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("admissionDate")
    val admissionDate: String?,
    @SerializedName("admissionNo")
    val admissionNo: String?,
    @SerializedName("birthPlace")
    val birthPlace: String?,
    @SerializedName("bloodGroup")
    val bloodGroup: String?,
    @SerializedName("canChangeCoverImg")
    val canChangeCoverImg: Boolean?,
    @SerializedName("caste")
    val caste: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("className")
    val className: String?,
    @SerializedName("classification")
    val classification: String?,
    @SerializedName("peN_Number")
    val peN_Number: String?,
    @SerializedName("contactEmailID")
    val contactEmailID: String?,
    @SerializedName("contactMobile")
    val contactMobile: String?,
    @SerializedName("billNumber")
    val billNumber: String?,
    @SerializedName("apaaR_ID")
    val apaaRID: String?,
    @SerializedName("contactPerson")
    val contactPerson: String?,
    @SerializedName("coverImg")
    val coverImg: String?,
    @SerializedName("diseNo")
    val diseNo: String?,
    @SerializedName("dob")
    val dob: String?,
    @SerializedName("fatherAadhaarNumber")
    val fatherAadhaarNumber: String?,
    @SerializedName("fatherPAN")
    val fatherPAN: String?,
    @SerializedName("fatherAnnualIncome")
    val fatherAnnualIncome: String?,
    @SerializedName("fatherDOB")
    val fatherDOB: Any?,
    @SerializedName("fatherDesignation")
    val fatherDesignation: String?,
    @SerializedName("fatherDesignationID")
    val fatherDesignationID: Any?,
    @SerializedName("fatherEmail_1")
    val fatherEmail1: String?,
    @SerializedName("fatherEmail_2")
    val fatherEmail2: String?,
    @SerializedName("fatherMob_1")
    val fatherMob1: String?,
    @SerializedName("fatherMob_2")
    val fatherMob2: String?,
    @SerializedName("fatherName")
    val fatherName: String?,
    @SerializedName("fatherOfficeAddress")
    val fatherOfficeAddress: String?,
    @SerializedName("fatherProfession")
    val fatherProfession: String?,
    @SerializedName("fatherResidentialAddress")
    val fatherResidentialAddress: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("house")
    val house: String?,
    @SerializedName("isBoarding")
    val isBoarding: Boolean?,
    @SerializedName("motherAadhaarNumber")
    val motherAadhaarNumber: String?,
    @SerializedName("motherAnnualIncome")
    val motherAnnualIncome: String?,
    @SerializedName("motherDOB")
    val motherDOB: Any?,
    @SerializedName("motherDesignation")
    val motherDesignation: String?,
    @SerializedName("motherEmail_1")
    val motherEmail1: String?,
    @SerializedName("motherEmail_2")
    val motherEmail2: String?,
    @SerializedName("motherMob_1")
    val motherMob1: String?,
    @SerializedName("srN_UMRN_SATSNumber")
    val srN_UMRN_SATSNumber: String?,
    @SerializedName("motherMob_2")
    val motherMob2: String?,
    @SerializedName("motherName")
    val motherName: String?,
    @SerializedName("motherOfficeAddress")
    val motherOfficeAddress: String?,
    @SerializedName("motherProfession")
    val motherProfession: String?,
    @SerializedName("motherResidentialAddress")
    val motherResidentialAddress: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("nationality")
    val nationality: String?,
    @SerializedName("parentAnniversaryDate")
    val parentAnniversaryDate: Any?,
    @SerializedName("parentStaus")
    val parentStaus: String?,
    @SerializedName("permanentAddress")
    val permanentAddress: String?,
    @SerializedName("permanentCity")
    val permanentCity: String?,
    @SerializedName("permanentState")
    val permanentState: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("previousSchoolDTL")
    val previousSchoolDTL: PreviousSchoolDTL?,
    @SerializedName("religion")
    val religion: String?,
    @SerializedName("rollNo")
    val rollNo: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("transport")
    val transport: String?,
    @SerializedName("username")
    val username: Any?
)

data class PreviousSchoolDTL(
    @SerializedName("address")
    val address: Any?,
    @SerializedName("board")
    val board: Any?,
    @SerializedName("schoolName")
    val schoolName: Any?
)