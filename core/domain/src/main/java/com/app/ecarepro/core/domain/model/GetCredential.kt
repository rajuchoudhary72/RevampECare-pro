package com.app.ecarepro.core.domain.model


data class GetCredential(
    val email: String? = null,
    val mobile: String? = null,
    val receivedOn: String,
    val schoolCode: String,
    val userType: Int,
)