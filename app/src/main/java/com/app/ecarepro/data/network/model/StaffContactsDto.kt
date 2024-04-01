package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class StaffContactsDto(
    @SerializedName("contacts")
    val contacts: List<Contact>?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

data class Contact(
    @SerializedName("admissionNo")
    val admissionNo: String?,
    @SerializedName("childName")
    val childName: String?,
    @SerializedName("childPhoto")
    val childPhoto: String?,
    @SerializedName("className")
    val className: String?,
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("hasRead")
    val hasRead: Boolean?,
    @SerializedName("isSelect")
    val isSelect: Boolean?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("readAt")
    val readAt: String?,
    @SerializedName("receiverID")
    val receiverID: String?,
    @SerializedName("receiverType")
    val receiverType: Int?,
    @SerializedName("rollNumber")
    val rollNumber: String?
) : Serializable {

    fun isParent() = childName.isNullOrBlank().not()
    fun isStaff() = designation.isNullOrBlank().not()
}

data class ContactsDto(
    val contacts: List<Contact>
) : Serializable