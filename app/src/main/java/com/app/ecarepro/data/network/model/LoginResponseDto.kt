package com.app.ecarepro.data.network.model

import com.app.ecarepro.data.database.model.UserEntity
import com.google.gson.annotations.SerializedName


data class LoginResponseDto(
    @SerializedName("authToken")
    val authToken: String?,
    @SerializedName("sessionID")
    val sessionID: String?,
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
    @SerializedName("photoPath")
    val photo: String?,
    @SerializedName("mobileNumer")
    val mobileNumer: String?,
    @SerializedName("classID")
    val classID: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("loginTime")
    val loginTime: String?,
    @SerializedName("class")
    val className: String?,
    @SerializedName("stName")
    val stName: String?,
    @SerializedName("schoolCode")
    val schoolCode: String? = null
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
        schoolCode = "",
        mobileNumber = mobileNumer,
        classID = classID,
        loginTime = loginTime,
        stName = stName,
        className = className,
        sessionId = sessionID
    )
}