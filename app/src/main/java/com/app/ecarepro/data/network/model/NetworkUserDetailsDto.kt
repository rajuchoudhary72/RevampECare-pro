package com.app.ecarepro.data.network.model

import com.app.ecarepro.data.database.model.UserEntity
import com.google.gson.annotations.SerializedName


data class NetworkUserDetailsDto(
    @SerializedName("errorCode")
    val errorCode: Int? = 0,
    @SerializedName("isVerified")
    val isVerified: Boolean?,
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("name")
    val name: String?,
    @SerializedName("userID")
    val userId: Int,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("roleName")
    val roleName: String?,
    @SerializedName("mobileNumer")
    val mobileNumber: String?,
    @SerializedName("classID")
    val classID: String?,
    @SerializedName("status")
    val status: String? = "0",
    val authToken: String?,
    val schoolCode: String?,
    val isUserAuthenticated: Boolean?,

    @SerializedName("userType")
    val userType: Int,

    val school: NetworkSchool? = null
) {
    fun getUserTypeName() = when (userType) {
        1 -> "Student"
        2 -> "Parent"
        else -> "Staff"
    }
}

fun NetworkUserDetailsDto.asUserEntity(): UserEntity {
    return UserEntity(
        userId = userType,
        name = name,
        photo = photo,
        userType = userType,
        authToken = authToken,
        isVerified = isVerified,
        isUserAuthenticated = isUserAuthenticated,
        roleName = roleName,
        schoolCode = schoolCode,
        mobileNumber = mobileNumber,
        classID = classID
    )
}