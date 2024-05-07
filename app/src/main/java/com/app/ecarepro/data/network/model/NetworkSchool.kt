package com.app.ecarepro.data.network.model

import com.app.ecarepro.data.database.model.SchoolEntity
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


data class NetworkSchool(
    @SerializedName("active")
    val active: Int?,
    @SerializedName("assessmentMarksURL")
    val assessmentMarksURL: String?,
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
    val feeReportURL: String?,
    @SerializedName("isBoardingSchool")
    val isBoardingSchool: Boolean?,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("logoNScName")
    val logoNScName: String?,
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
    val schoolCode: String,
    @SerializedName("schoolName")
    val schoolName: String?,
    @SerializedName("slider")
    val slider: List<Slider>?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("supportDays")
    val supportDays: String?,
    @SerializedName("supportEmail")
    val supportEmail: String?,
    @SerializedName("supportHours")
    val supportHours: String?,
    @SerializedName("supportPhone")
    val supportPhone: String?,
    @SerializedName("themColor")
    val themColor: String?,
    @SerializedName("webSite")
    val webSite: String?,
    val isSelected: Boolean = false
)

data class Slider(
    @SerializedName("description")
    val description: String?,
    @SerializedName("imgPath")
    val imgPath: String?,
    @SerializedName("module")
    val module: String
)

fun NetworkSchool.asNetworkSchool(): SchoolEntity {
    return SchoolEntity(
        schoolCode = schoolCode,
        active = active,
        assessmentMarksURL = assessmentMarksURL,
        city = city,
        contactEmail = contactEmail,
        eCareProSch = eCareProSch,
        feePaymentURL = feePayemtURL,
        feeReportURL = feeReportURL,
        isBoardingSchool = isBoardingSchool,
        logo = logo,
        logoNScName = logoNScName,
        logoScName = logoScName,
        marksEntryURL = marksEntryURL,
        schAdd1 = schAdd1,
        schAdd2 = schAdd2,
        schUpdatedOn = schUpdatedOn,
        schoolName = schoolCode,
        state = state,
        supportEmail = supportEmail,
        supportHours = supportHours,
        supportPhone = supportPhone,
        supportDays = supportDays,
        themColor = themColor,
        webSite = webSite,
        slides = Gson().toJson(slider)
    )
}