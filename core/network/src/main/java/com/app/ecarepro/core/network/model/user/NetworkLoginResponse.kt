package com.app.ecarepro.core.network.model.user

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class NetworkLoginResponse(
    @SerialName("authenticated")
    val authenticated: Boolean?,
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("isDefaulter")
    val isDefaulter: Boolean?,
    @SerialName("isOTPEnabled")
    val isOTPEnabled: Boolean?,
    @SerialName("isOTPValidated")
    val isOTPValidated: Boolean?,
    @SerialName("message")
    override val message: String,
    @SerialName("otpAuthKey")
    val otpAuthKey: String?,
    @SerialName("otpMode")
    val otpMode: Int?,
    @SerialName("remainAttampts")
    val remainAttempts: Int?,
    @SerialName("resendWaitSeconds")
    val resendWaitSeconds: Int?,
    @SerialName("schCode")
    val schCode: String?,
    @SerialName("status")
    override val status: String,
    @SerialName("userDTL")
    val userDTL: UserDetails?,
) : NetworkResponse

