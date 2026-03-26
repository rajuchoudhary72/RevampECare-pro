package com.app.ecarepro.core.network.model.admin

import com.app.ecarepro.core.domain.model.RollNumberStudent
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGetRollNumberStudents(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("canAutoAssignRollNo") val canAutoAssignRollNo: Boolean?,
    @SerialName("students") val students: List<NetworkRollNumberStudent>?,
) : NetworkResponse

@Serializable
data class NetworkRollNumberStudent(
    @SerialName("stID") val stID: Int,
    @SerialName("name") val name: String,
    @SerialName("rollNumber") val rollNumber: String?,
    @SerialName("admissionNumber") val admissionNumber: String?,
    @SerialName("photo") val photo: String?,
    @SerialName("houseID") val houseID: Int?,
    @SerialName("isSelected") val isSelected: Boolean?,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("fatherPhoto") val fatherPhoto: String? = null,
)

@Serializable
data class NetworkAssignRollNumberItem(
    @SerialName("stID") val stID: Int,
    @SerialName("houseID") val houseID: Int,
    @SerialName("rollNumber") val rollNumber: String,
)

fun NetworkRollNumberStudent.toDomainModel() = RollNumberStudent(
    stID = stID,
    name = name,
    rollNumber = rollNumber ?: "",
    admissionNumber = admissionNumber ?: "",
    photo = photo ?: "",
    houseID = houseID ?: 0,
    isSelected = isSelected ?: false,
)
