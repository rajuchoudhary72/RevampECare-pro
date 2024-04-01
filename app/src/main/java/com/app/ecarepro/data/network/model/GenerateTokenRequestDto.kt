package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName
import com.app.ecarepro.utils.Constant.Companion.SMS_USER_PASSWORD
import com.app.ecarepro.utils.Constant.Companion.SMS_USER_USER_NAME

data class GenerateTokenRequestDto(
    @SerializedName("ErrorCode")
    val errorCode: Int? = 0,
    @SerializedName("Password")
    val password: String? = SMS_USER_PASSWORD,
    @SerializedName("Username")
    val username: String? = SMS_USER_USER_NAME

)