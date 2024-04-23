package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class ContactWithClassDto(
    @SerializedName("classContacts")
    val classContacts: List<ClassContact>?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

data class ClassContact(
    @SerializedName("classID")
    val classID: Int,
    @SerializedName("className")
    val className: String?,
    @SerializedName("contacts")
    val contacts: List<Contact>?
)