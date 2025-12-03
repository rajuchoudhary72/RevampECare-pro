package com.app.ecarepro.core.network.model.staff

import com.app.ecarepro.core.domain.model.Section
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi
data class NetworkClassSection(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("sections")
    val sections: List<NetworkSection>?,
    @SerialName("status")
    override val status: String,
): NetworkResponse

@Serializable
@InternalSerializationApi
data class NetworkSection(
    @SerialName("classID")
    val classID: Int?,
    @SerialName("isSelected")
    val isSelected: Boolean?,
    @SerialName("secID")
    val secID: Int?,
    @SerialName("secName")
    val secName: String?,
)

fun NetworkSection.toDomainModel() = Section(
    classID = classID,
    isSelected = isSelected,
    secID = secID,
    secName = secName
)

