package com.app.ecarepro.ui.edit_profile.staff.model

data class StaffProfileModel(
    val canEditProfile: Boolean?,
    val errorCode: Int?,
    val message: String?,
    val profile: Profile?,
    val status: String?
)