package com.app.ecarepro.core.network.model.admin

import com.app.ecarepro.core.domain.model.HouseItem
import com.app.ecarepro.core.domain.model.HouseStudent
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGetHouseStudents(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("houses") val houses: List<NetworkHouseItem>?,
    @SerialName("students") val students: List<NetworkHouseStudent>?,
) : NetworkResponse

@Serializable
data class NetworkHouseItem(
    @SerialName("houseID") val houseID: Int,
    @SerialName("houseName") val houseName: String?,
)

@Serializable
data class NetworkHouseStudent(
    @SerialName("stID") val stID: Int,
    @SerialName("name") val name: String,
    @SerialName("photo") val photo: String?,
    @SerialName("admissionNumber") val admissionNumber: String?,
    @SerialName("rollNumber") val rollNumber: String?,
    @SerialName("houseID") val houseID: Int?,
    @SerialName("houseName") val houseName: String?,
)

@Serializable
data class NetworkAssignHouseItem(
    @SerialName("stID") val stID: Int,
    @SerialName("houseID") val houseID: Int,
    @SerialName("rollNumber") val rollNumber: String,
)

fun NetworkHouseItem.toDomainModel() = HouseItem(
    houseID = houseID,
    houseName = houseName ?: "",
)

fun NetworkHouseStudent.toDomainModel() = HouseStudent(
    stID = stID,
    name = name,
    photo = photo ?: "",
    admissionNumber = admissionNumber ?: "",
    rollNumber = rollNumber ?: "",
    houseID = houseID ?: 0,
    houseName = houseName ?: "",
)
