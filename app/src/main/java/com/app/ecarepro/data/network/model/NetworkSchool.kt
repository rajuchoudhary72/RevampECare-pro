package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class NetworkSchool(
    @SerializedName("active")
    val active: Int?,
    @SerializedName("assessmentMarksURL")
    val assessmentMarksURL: Any?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("contactEmail")
    val contactEmail: String?,
    @SerializedName("eCareProSch")
    val eCareProSch: Boolean?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("feePayemtURL")
    val feePayemtURL: String?,
    @SerializedName("feeReportURL")
    val feeReportURL: Any?,
    @SerializedName("isBoardingSchool")
    val isBoardingSchool: Boolean?,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("logoNScName")
    val logoNScName: Any?,
    @SerializedName("logoScName")
    val logoScName: String?,
    @SerializedName("marksEntryURL")
    val marksEntryURL: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("schAdd_1")
    val schAdd1: String?,
    @SerializedName("schAdd_2")
    val schAdd2: String?,
    @SerializedName("schUpdatedOn")
    val schUpdatedOn: String?,
    @SerializedName("schoolCode")
    val schoolCode: String?,
    @SerializedName("schoolName")
    val schoolName: String?,
    @SerializedName("slider")
    val slider: List<Slider?>?,
    @SerializedName("state")
    val state: Any?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("supportDays")
    val supportDays: Any?,
    @SerializedName("supportEmail")
    val supportEmail: String?,
    @SerializedName("supportHours")
    val supportHours: Any?,
    @SerializedName("supportPhone")
    val supportPhone: String?,
    @SerializedName("themColor")
    val themColor: Any?,
    @SerializedName("webSite")
    val webSite: String?
)

data class Slider(
    @SerializedName("description")
    val description: String?,
    @SerializedName("imgPath")
    val imgPath: String?,
    @SerializedName("module")
    val module: String?
)