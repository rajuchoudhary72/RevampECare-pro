package com.app.ecarepro.ui.edit_profile.model.update_profile

data class UpdateProfileModel(
    var fatherDesignationID: Int,
    var fatherProfessionID: Int,
    var motherDesignationID: Int,
    var motherProfessionID: Int,
    var parentStausID: Int,
    var stuBloodGroupID: Int,
    var stuReligionID: Int,
    var studentProfile: StudentProfile
)