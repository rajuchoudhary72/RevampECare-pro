package com.app.ecarepro.ui.edit_profile.model

data class Profile(
    var bloodGroupLST: List<BloodGroupLST>,
    var canChangeChildImg: Boolean,
    var canChangeCoverImg: Boolean,
    var canChangeProfileImg: Boolean,
    var designationLST: List<DesignationLST>,
    var fatherDesignationID: Int,
    var fatherProfessionID: Int,
    var motherDesignationID: Int,
    var motherProfessionID: Int,
    var name: String,
    var parentStausID: Int,
    var parentsStatusLST: List<ParentsStatusLST>,
    var photo: String,
    var professionLST: List<ProfessionLST>,
    var profileUpdationRecord: List<ProfileUpdationRecord>,
    var relegionLST: List<RelegionLST>,
    var socialMedia: SocialMedia,
    var stuBloodGroupID: Int,
    var stuReligionID: Int,
    var studentProfile: StudentProfile,
    var userImgReq: UserImgReq,
    var username: String
)