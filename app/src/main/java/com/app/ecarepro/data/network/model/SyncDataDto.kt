package com.app.ecarepro.data.network.model

import com.app.ecarepro.data.database.model.UserEntity
import com.google.gson.annotations.SerializedName


data class SyncDataDto(
    @SerializedName("authenticated")
    val authenticated: Boolean?,
    @SerializedName("data")
    val data: SyncData?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

data class SyncData(
    @SerializedName("authToken")
    val authToken: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("classID")
    val classID: String?,
    @SerializedName("class")
    val classX: String?,
    @SerializedName("feePaymentURL")
    val feePaymentURL: String?,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("logoScName")
    val logoScName: String?,
    @SerializedName("marksEntryURL")
    val marksEntryURL: String?,
    @SerializedName("mobileNumer")
    val mobileNumer: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photoPath")
    val photoPath: String?,
    @SerializedName("roleName")
    val roleName: String?,
    @SerializedName("sChAdd2")
    val sChAdd2: String?,
    @SerializedName("schAdd1")
    val schAdd1: String?,
    @SerializedName("schoolCode")
    val schoolCode: String,
    @SerializedName("stName")
    val stName: String?,
    @SerializedName("userID")
    val userID: Int,
    @SerializedName("userType")
    val userType: Int,
    @SerializedName("website")
    val website: String?
)

fun SyncData.asUserEntity(): UserEntity {
    return UserEntity(
        userId = userID,
        name = name,
        photo = photoPath,
        userType = userType,
        authToken = authToken,
        isVerified = false,
        isUserAuthenticated = false,
        roleName = roleName,
        schoolCode = schoolCode,
        mobileNumber = mobileNumer,
        classID = classID,
        loginTime = "",
        stName = stName,
        className = classX
    )
}