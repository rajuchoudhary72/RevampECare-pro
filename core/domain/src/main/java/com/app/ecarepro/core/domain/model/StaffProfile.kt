package com.app.ecarepro.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StaffProfile(
    val id: String,
    val sid: Int,
    val staffId: String?,
    val name: String,
    val designation: String,
    val staffType: String,
    val gender: String?,
    val maritalStatus: String?,
    val mobile: String?,
    val email: String?,
    val qualification: String?,
    val teachersSubject: String?,
    val photo: String?
) {
    val displayName: String get() = name
}
