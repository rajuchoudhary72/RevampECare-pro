package com.app.ecarepro.core.network.model.staff

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class NetworkClasses(
    @SerialName("editMode")
    val editMode: Boolean?,
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("myClasses")
    val myClasses: List<NetworkClass>?,
    @SerialName("openPreviousDay")
    val openPreviousDay: Boolean?,
    @SerialName("status")
    override val status: String,
) : NetworkResponse

@InternalSerializationApi
@Serializable
data class NetworkClass(
    @SerialName("classID")
    val classID: Int?,
    @SerialName("className")
    val className: String?,
    @SerialName("id")
    val id: String,
    @SerialName("isSelect")
    val isSelect: Boolean?,
)

fun NetworkClass.toDomainModel() = com.app.ecarepro.core.domain.model.Class(
    className = className,
    id = id,
    isSelect = isSelect,
    classID = classID
)

