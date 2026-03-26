package com.app.ecarepro.core.domain.model

data class SavedAccountItem(
    val localId: Int,
    val name: String,
    val roleName: String,
    val schoolCode: String,
    val schoolName: String,
    val schoolLogo: String?,
    val userPhoto: String?,
    val isActive: Boolean,
    val userType: Int?,
    val className: String?,
    val stName: String?,
)
