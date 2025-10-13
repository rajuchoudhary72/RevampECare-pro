package com.app.ecarepro.core.network.model.user

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@InternalSerializationApi
@Serializable
data class UserDetails(
    @SerialName("authToken")
    val authToken: String?,
    @SerialName("authenticated")
    val authenticated: Boolean?,
    @SerialName("classID")
    val classID: String?,
    @SerialName("class")
    val classX: String?,
    @SerialName("errorCode")
    val errorCode: Int?,
    @SerialName("isDefaulter")
    val isDefaulter: Boolean?,
    @SerialName("message")
    val message: String?,
    @SerialName("mobileNumer")
    val mobileNumber: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("photoPath")
    val photoPath: String?,
    @SerialName("roleName")
    val roleName: String?,
    @SerialName("sessionID")
    val sessionID: String?,
    @SerialName("stName")
    val stName: String?,
    @SerialName("status")
    val status: String?,
    @SerialName("userID")
    val userID: Int?,
    @SerialName("userType")
    val userType: Int?,
)

