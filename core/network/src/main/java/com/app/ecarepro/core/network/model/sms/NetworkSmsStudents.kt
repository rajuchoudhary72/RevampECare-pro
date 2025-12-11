package com.app.ecarepro.core.network.model.sms

import com.app.ecarepro.core.domain.model.Student
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi
data class NetworkSmsStudents(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("students")
    val students: List<NetworkStudent>?,
): NetworkResponse



@Serializable
@InternalSerializationApi
data class NetworkStudent(
    @SerialName("admissionNumber")
    val admissionNumber: String?,
    @SerialName("classID")
    val classID: Int?,
    @SerialName("class")
    val classX: String?,
    @SerialName("contactPerson")
    val contactPerson: String?,
    @SerialName("dob")
    val dob: String?,
    @SerialName("emailID")
    val emailID: String?,
    @SerialName("fatherName")
    val fatherName: String?,
    @SerialName("isSelected")
    val isSelected: Boolean?,
    @SerialName("mobileNumber")
    val mobileNumber: String?,
    @SerialName("msPassword")
    val msPassword: String?,
    @SerialName("msUser")
    val msUser: String?,
    @SerialName("recipientName")
    val recipientName: String?,
    @SerialName("recipientPhoto")
    val recipientPhoto: String?,
    @SerialName("rollNumber")
    val rollNumber: String?,
    @SerialName("stID")
    val stID: Int?,
    @SerialName("studentName")
    val studentName: String?,
)

fun NetworkStudent.toDomainModel() = Student(
    admissionNumber = admissionNumber.orEmpty(),
    classID = classID ?: 0,
    className = classX.orEmpty(),
    contactPerson = contactPerson.orEmpty(),
    dob = dob.orEmpty(),
    emailID = emailID.orEmpty(),
    fatherName = fatherName.orEmpty(),
    isSelected = isSelected ?: false,
    mobileNumber = mobileNumber.orEmpty(),
    msPassword = msPassword.orEmpty(),
    msUser = msUser.orEmpty(),
    recipientName = recipientName.orEmpty(),
    recipientPhoto = recipientPhoto.orEmpty(),
    rollNumber = rollNumber.orEmpty(),
    stID = stID ?: 0,
    studentName = studentName.orEmpty(),
)


