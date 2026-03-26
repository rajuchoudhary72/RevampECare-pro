package com.app.ecarepro.core.domain.model

data class StaffType(val staffTypeID: Int, val staffType: String)

data class StaffContact(
    val receiverID: Int,
    val receiverType: Int,
    val name: String,
    val designation: String?,
    val staffTypeID: Int?,
    val photo: String?,
)

data class StudentParentContact(
    val receiverID: Int,
    val receiverType: Int,
    val name: String,
    val classID: Int?,
    val className: String?,
    val childName: String?,
    val photo: String?,
    val rollNumber: String?,
)

data class CreateCircularData(
    val isBoardingSchool: Boolean,
    val staffTypes: List<StaffType>,
    val classes: List<Class>,
)

data class SaveCircularRequest(
    val title: String,
    val details: String? = null,
    val releadOn: String,
    val recipientType: String,
    val status: Boolean,
    val mustRead: Boolean,
    val scholarType: String,
    val siDs: String? = null,
    val classIDs: String? = null,
    val stIDs: String? = null,
    val browsedFile: CircularAttachment? = null,
)

data class CircularAttachment(val attachment: String, val fileExt: String)
