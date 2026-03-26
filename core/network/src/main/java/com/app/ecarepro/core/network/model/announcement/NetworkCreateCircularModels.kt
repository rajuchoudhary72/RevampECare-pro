package com.app.ecarepro.core.network.model.announcement

import com.app.ecarepro.core.domain.model.CircularAttachment
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.CreateCircularData
import com.app.ecarepro.core.domain.model.SaveCircularRequest
import com.app.ecarepro.core.domain.model.StaffContact
import com.app.ecarepro.core.domain.model.StaffType
import com.app.ecarepro.core.domain.model.StudentParentContact
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCreateCircularResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("isBoardingSchool") val isBoardingSchool: Boolean? = null,
    @SerialName("staffTypes") val staffTypes: List<NetworkStaffType>? = null,
    @SerialName("classes") val classes: List<NetworkCreateCircularClass>? = null,
) : NetworkResponse

@Serializable
data class NetworkStaffType(
    @SerialName("staffTypeID") val staffTypeID: Int,
    @SerialName("staff_Type") val staffType: String,
)

@Serializable
data class NetworkCreateCircularClass(
    @SerialName("classID") val classID: Int,
    @SerialName("className") val className: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("isSelect") val isSelect: Boolean = false,
)

@Serializable
data class NetworkStaffContactResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("contacts") val contacts: List<NetworkStaffContact>? = null,
) : NetworkResponse

@Serializable
data class NetworkStaffContact(
    @SerialName("receiverID") val receiverID: Int,
    @SerialName("receiverType") val receiverType: Int,
    @SerialName("name") val name: String,
    @SerialName("designation") val designation: String? = null,
    @SerialName("staffTypeID") val staffTypeID: Int? = null,
    @SerialName("photo") val photo: String? = null,
)

@Serializable
data class NetworkStudentParentContactResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("contacts") val contacts: List<NetworkStudentParentContact>? = null,
) : NetworkResponse

@Serializable
data class NetworkStudentParentContact(
    @SerialName("receiverID") val receiverID: Int,
    @SerialName("receiverType") val receiverType: Int,
    @SerialName("name") val name: String,
    @SerialName("classID") val classID: Int? = null,
    @SerialName("className") val className: String? = null,
    @SerialName("childName") val childName: String? = null,
    @SerialName("rollNumber") val rollNumber: String? = null,
    @SerialName("photo") val photo: String? = null,
)

@Serializable
data class NetworkSaveCircularRequest(
    @SerialName("title") val title: String,
    @SerialName("details") val details: String? = null,
    @SerialName("releadOn") val releadOn: String,
    @SerialName("recipientType") val recipientType: String,
    @SerialName("status") val status: Boolean,
    @SerialName("mustRead") val mustRead: Boolean,
    @SerialName("scholarType") val scholarType: String,
    @SerialName("siDs") val siDs: String? = null,
    @SerialName("classIDs") val classIDs: String? = null,
    @SerialName("stIDs") val stIDs: String? = null,
    @SerialName("browsedFile") val browsedFile: NetworkCircularAttachment? = null,
)

@Serializable
data class NetworkCircularAttachment(
    @SerialName("attachment") val attachment: String,
    @SerialName("fileExt") val fileExt: String,
)

@Serializable
data class NetworkSaveCircularResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
) : NetworkResponse

fun NetworkStaffType.toDomainModel() = StaffType(staffTypeID = staffTypeID, staffType = staffType)

fun NetworkStaffContact.toDomainModel() = StaffContact(
    receiverID = receiverID,
    receiverType = receiverType,
    name = name,
    designation = designation,
    staffTypeID = staffTypeID,
    photo = photo,
)

fun NetworkStudentParentContact.toDomainModel() = StudentParentContact(
    receiverID = receiverID,
    receiverType = receiverType,
    name = name,
    classID = classID,
    className = className,
    childName = childName,
    photo = photo,
    rollNumber = rollNumber,
)

fun NetworkCreateCircularResponse.toDomainModel() = CreateCircularData(
    isBoardingSchool = isBoardingSchool ?: false,
    staffTypes = staffTypes?.map { it.toDomainModel() } ?: emptyList(),
    classes = classes?.map {
        Class(classID = it.classID, className = it.className, id = it.id ?: "", isSelect = it.isSelect)
    } ?: emptyList(),
)

fun SaveCircularRequest.toNetworkModel() = NetworkSaveCircularRequest(
    title = title,
    details = details,
    releadOn = releadOn,
    recipientType = recipientType,
    status = status,
    mustRead = mustRead,
    scholarType = scholarType,
    siDs = siDs,
    classIDs = classIDs,
    stIDs = stIDs,
    browsedFile = browsedFile?.let { NetworkCircularAttachment(it.attachment, it.fileExt) },
)
