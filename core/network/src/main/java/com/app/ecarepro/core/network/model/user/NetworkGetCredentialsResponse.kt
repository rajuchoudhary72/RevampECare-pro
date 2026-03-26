package com.app.ecarepro.core.network.model.user

import com.app.ecarepro.core.domain.model.Ward
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@InternalSerializationApi
@Serializable
data class NetworkGetCredentialsResponse(
    override val errorCode: Int,
    override val message: String,
    override val status: String,
    @SerialName("users")
    val wards: List<NetworkWard>?,
) : NetworkResponse


@InternalSerializationApi
@Serializable
data class NetworkWard(
    @SerialName("childName")
    val childName: String?,
    @SerialName("class")
    val classX: String?,
    @SerialName("memberName")
    val memberName: String?,
    @SerialName("photo")
    val photo: String?,
    @SerialName("userID")
    val userID: Int,
    @SerialName("userType")
    val userType: Int,
)

fun NetworkWard.toWard() = Ward(
    childName = childName,
    classX = classX,
    memberName = memberName,
    photo = photo,
    userID = userID,
    userType = userType
)