package com.app.ecarepro.data.network.model.submit_assignment
import com.app.ecarepro.data.database.model.UserEntity
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.google.gson.annotations.SerializedName

data class TwoFactorLoginResponseDto(
    @SerializedName("authenticated")
    val authenticated: Boolean?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("isOTPEnabled")
    val isOTPEnabled: Boolean?,
    @SerializedName("isOTPValidated")
    val isOTPValidated: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("otpAuthKey")
    val otpAuthKey: String?,
    @SerializedName("otpMode")
    val otpMode: Int?,
    @SerializedName("remainAttampts")
    val remainAttampts: Int?,
    @SerializedName("schCode")
    val schCode: Any?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("userDTL")
    val userDTL: UserDTL?
)
data class UserDTL(
    @SerializedName("authToken")
    val authToken: String?,
    @SerializedName("authenticated")
    val authenticated: Boolean?,
    @SerializedName("classID")
    val classID: Int?,
    @SerializedName("class")
    val classX: String?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: Any?,
    @SerializedName("mobileNumer")
    val mobileNumer: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photoPath")
    val photoPath: String?,
    @SerializedName("roleName")
    val roleName: String?,
    @SerializedName("stName")
    val stName: String?,
    @SerializedName("status")
    val status: Any?,
    @SerializedName("userID")
    val userID: Int,
    @SerializedName("userType")
    val userType: Int?
)
fun UserDTL.asUserEntity(): UserEntity {
    return UserEntity(
        userId = userID?:0,
        name = name,
        photo = photoPath,
        userType = userType?:0,
        authToken = authToken,
        isVerified = false,
        isUserAuthenticated = false,
        roleName = roleName,
        schoolCode = "",
        mobileNumber = mobileNumer,
        classID = classID.toString(),
        loginTime = System.currentTimeMillis().toString(),
        stName = stName,
        className = classX
    )
}