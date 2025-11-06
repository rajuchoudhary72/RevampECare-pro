package com.app.ecarepro.core.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class LoginResult(
    val authenticated: Boolean?,
    val errorCode: Int,
    val isDefaulter: Boolean?,
    val isOTPEnabled: Boolean?,
    val isOTPValidated: Boolean?,
    val message: String,
    val otpAuthKey: String?,
    val otpMode: Int?,
    val remainAttempts: Int?,
    val resendWaitSeconds: Int?,
    val schCode: String?,
    val status: String,
    val userDetail: User?,
)