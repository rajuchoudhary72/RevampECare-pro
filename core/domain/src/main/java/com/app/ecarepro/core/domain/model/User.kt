package com.app.ecarepro.core.domain.model

import java.io.Serializable

@kotlinx.serialization.Serializable
data class User(
    val authToken: String?,
    val authenticated: Boolean?,
    val classID: String?,
    val className: String?,
    val isDefaulter: Boolean?,
    val message: String?,
    val mobileNumber: String?,
    val name: String?,
    val photoPath: String?,
    val roleName: String?,
    val sessionID: String?,
    val stName: String?,
    val status: String?,
    val userID: Int,
    val userType: Int?,
    val schoolCode: String,
) : Serializable

