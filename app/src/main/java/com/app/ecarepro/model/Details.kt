package com.app.ecarepro.model

import com.app.ecarepro.model.UserImgReq

data class Details(
    val aadharCardNo: String,
    val address: String,
    val alternateEmailID: String,
    val alternateMobile: String,
    val canChangeCoverImg: Boolean,
    val canChangeProfileImg: Boolean,
    val coverImg: String,
    val designation: String,
    val doAnniversary: String,
    val dob: String,
    val doj: String,
    val emailID: String,
    val emergencyContactNo: String,
    val fName: String,
    val fatherHusbandMob: String,
    val fatherHusbandName: String,
    val gender: String,
    val isSpouseName: Boolean,
    val lName: String,
    val mName: String,
    val maritalStatus: String,
    val mobile: String,
    val name: String,
    val nationality: String,
    val p_Address: String,
    val paN_Number: String,
    val photo: String,
    val qualification: String,
    val religion: String,
    val roleName: String,
    val title: String,
    val userImgReq: UserImgReq,
    val username: String
)