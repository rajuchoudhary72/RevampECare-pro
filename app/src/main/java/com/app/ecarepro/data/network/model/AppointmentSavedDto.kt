package com.app.ecarepro.data.network.model
import com.google.gson.annotations.SerializedName


data class AppointmentSavedDto(
    @SerializedName("data")
    val `data`: AppointmentSavedData?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
)

data class AppointmentSavedData(
    @SerializedName("appdetails")
    val appdetails: Appdetails?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
)

data class Appdetails(
    @SerializedName("address")
    val address: String?,
    @SerializedName("appointmentDuration")
    val appointmentDuration: String?,
    @SerializedName("appointmentStatus")
    val appointmentStatus: String?,
    @SerializedName("appointmentid")
    val appointmentid: Int?,
    @SerializedName("appointmentno")
    val appointmentno: String?,
    @SerializedName("autoOutTime")
    val autoOutTime: Boolean?,
    @SerializedName("checkInTime")
    val checkInTime: String?,
    @SerializedName("checkOutTime")
    val checkOutTime: String?,
    @SerializedName("coVisitorone")
    val coVisitorone: String?,
    @SerializedName("coVisitorthree")
    val coVisitorthree: String?,
    @SerializedName("coVisitortwo")
    val coVisitortwo: String?,
    @SerializedName("company")
    val company: String?,
    @SerializedName("department")
    val department: String?,
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("employeeName")
    val employeeName: String?,
    @SerializedName("formtype")
    val formtype: String?,
    @SerializedName("idNo")
    val idNo: String?,
    @SerializedName("idType")
    val idType: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("noOfCovisitors")
    val noOfCovisitors: String?,
    @SerializedName("otherPurposeText")
    val otherPurposeText: String?,
    @SerializedName("purpose")
    val purpose: String?,
    @SerializedName("visitingDate")
    val visitingDate: String?,
    @SerializedName("visitingTime")
    val visitingTime: String?,
    @SerializedName("visitorPhoto")
    val visitorPhoto: String?,
    @SerializedName("visitorType")
    val visitorType: String?
)