package com.app.ecarepro.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSchoolList(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("list")
    val list: List<NetworkSchool>?,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
): NetworkResponse

@Serializable
data class NetworkSchool(
    @SerialName("address")
    val address: String?,
    @SerialName("city")
    val city: String?,
    @SerialName("logo")
    val logo: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("schoolCode")
    val schoolCode: String,
    @SerialName("state")
    val state: String?,
)



