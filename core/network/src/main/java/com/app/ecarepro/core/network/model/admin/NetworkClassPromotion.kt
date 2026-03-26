package com.app.ecarepro.core.network.model.admin

import com.app.ecarepro.core.domain.model.ClassPromotionData
import com.app.ecarepro.core.domain.model.NextSessionClass
import com.app.ecarepro.core.domain.model.PromotionSection
import com.app.ecarepro.core.domain.model.PromotionStudent
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGetClassPromotion(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("yrID") val yrID: Int?,
    @SerialName("session") val session: String?,
    @SerialName("nYrID") val nYrID: Int?,
    @SerialName("nextSession") val nextSession: String?,
    @SerialName("students") val students: List<NetworkPromotionStudent>?,
) : NetworkResponse

@Serializable
data class NetworkPromotionStudent(
    @SerialName("stID") val stID: Int,
    @SerialName("name") val name: String,
    @SerialName("class") val studentClass: String?,
    @SerialName("rollNumber") val rollNumber: String?,
    @SerialName("admissionNumber") val admissionNumber: String?,
    @SerialName("fatherName") val fatherName: String?,
    @SerialName("photo") val photo: String?,
    @SerialName("isSelected") val isSelected: Boolean?,
    @SerialName("nextSessionClasses") val nextSessionClasses: List<NetworkNextSessionClass>?,
)

@Serializable
data class NetworkNextSessionClass(
    @SerialName("classID") val classID: Int,
    @SerialName("className") val className: String,
    @SerialName("sections") val sections: List<NetworkPromotionSection>?,
    @SerialName("isSelected") val isSelected: Boolean?,
)

@Serializable
data class NetworkPromotionSection(
    @SerialName("classID") val classID: Int?,
    @SerialName("secID") val secID: Int,
    @SerialName("secName") val secName: String,
    @SerialName("isSelected") val isSelected: Boolean?,
)

@Serializable
data class NetworkSaveClassPromotionRequest(
    @SerialName("yrID") val yrID: Int,
    @SerialName("nYrID") val nYrID: Int,
    @SerialName("studentPromotedClasses") val studentPromotedClasses: List<NetworkStudentPromotedClass>,
)

@Serializable
data class NetworkStudentPromotedClass(
    @SerialName("stID") val stID: Int,
    @SerialName("newClassID") val newClassID: Int,
    @SerialName("newSectionID") val newSectionID: Int,
)

fun NetworkGetClassPromotion.toDomainModel() = ClassPromotionData(
    yrID = yrID ?: 0,
    session = session ?: "",
    nYrID = nYrID ?: 0,
    nextSession = nextSession ?: "",
    students = students?.map { it.toDomainModel() } ?: emptyList(),
)

fun NetworkPromotionStudent.toDomainModel() = PromotionStudent(
    stID = stID,
    name = name,
    studentClass = studentClass ?: "",
    rollNumber = rollNumber ?: "",
    admissionNumber = admissionNumber ?: "",
    fatherName = fatherName ?: "",
    photo = photo ?: "",
    isSelected = isSelected ?: false,
    nextSessionClasses = nextSessionClasses?.map { it.toDomainModel() } ?: emptyList(),
)

fun NetworkNextSessionClass.toDomainModel() = NextSessionClass(
    classID = classID,
    className = className,
    sections = sections?.map { it.toDomainModel() } ?: emptyList(),
    isSelected = isSelected ?: false,
)

fun NetworkPromotionSection.toDomainModel() = PromotionSection(
    classID = classID ?: 0,
    secID = secID,
    secName = secName,
    isSelected = isSelected ?: false,
)
