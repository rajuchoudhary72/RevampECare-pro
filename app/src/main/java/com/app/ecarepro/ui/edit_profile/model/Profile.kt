package com.app.ecarepro.ui.edit_profile.model

data class Profile(
    val bloodGroupLST: List<BloodGroupLST>,
    val canChangeChildImg: Boolean,
    val canChangeCoverImg: Boolean,
    val canChangeProfileImg: Boolean,
    val designationLST: List<DesignationLST>,
    val fatherDesignationID: Int,
    val fatherProfessionID: Int,
    val motherDesignationID: Int,
    val motherProfessionID: Int,
    val name: String,
    val parentStausID: Int,
    val parentsStatusLST: List<ParentsStatusLST>,
    val photo: String,
    val professionLST: List<ProfessionLST>,
    val profileUpdationRecord: List<ProfileUpdationRecord>,
    val relegionLST: List<RelegionLST>,
    val socialMedia: SocialMedia,
    val stuBloodGroupID: Int,
    val stuReligionID: Int,
    val studentProfile: StudentProfile,
    val userImgReq: UserImgReq,
    val username: String
)