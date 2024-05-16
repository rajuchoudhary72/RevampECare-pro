package com.app.ecarepro.data.network.model

import com.app.ecarepro.data.database.model.UserEntity
import com.google.gson.annotations.SerializedName


data class LoginResponseDto(
    @SerializedName("authToken")
    val authToken: String?,
    @SerializedName("authenticated")
    val authenticated: Boolean?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("userID")
    val userID: Int,
    @SerializedName("userType")
    val userType: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("roleName")
    val roleName: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("status")
    val status: String?
)

fun LoginResponseDto.asUserEntity(): UserEntity {
    return UserEntity(
        userId = userID,
        name = name,
        photo = photo,
        userType = userType,
        authToken = authToken,
        isVerified = false,
        isUserAuthenticated = false,
        roleName = roleName,
        schoolCode = ""
    )
}