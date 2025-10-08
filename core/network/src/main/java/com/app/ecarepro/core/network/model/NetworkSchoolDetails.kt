package com.app.ecarepro.core.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSchoolDetails(
    @SerialName("active")
    val active: Int?,
    @SerialName("assessmentMarksURL")
    val assessmentMarksURL: String?,
    @SerialName("city")
    val city: String?,
    @SerialName("contactEmail")
    val contactEmail: String?,
    @SerialName("eCareProSch")
    val eCareProSch: Boolean?,
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("feePayemtURL")
    val feePayemtURL: String?,
    @SerialName("feeReportURL")
    val feeReportURL: String?,
    @SerialName("isBoardingSchool")
    val isBoardingSchool: Boolean?,
    @SerialName("isStudentLoginBlocked")
    val isStudentLoginBlocked: Boolean?,
    @SerialName("logo")
    val logo: String?,
    @SerialName("logoNScName")
    val logoNScName: String?,
    @SerialName("logoScName")
    val logoScName: String?,
    @SerialName("marksEntryURL")
    val marksEntryURL: String?,
    @SerialName("message")
    override val message: String,
    @SerialName("schAdd_1")
    val schAdd1: String?,
    @SerialName("schAdd_2")
    val schAdd2: String?,
    @SerialName("schUpdatedOn")
    val schUpdatedOn: String?,
    @SerialName("schoolCode")
    val schoolCode: String?,
    @SerialName("schoolName")
    val schoolName: String?,
    @SerialName("slider")
    val slider: SchoolSlider?,
    @SerialName("state")
    val state: String?,
    @SerialName("status")
    override val status: String,
    @SerialName("supportDays")
    val supportDays: String?,
    @SerialName("supportEmail")
    val supportEmail: String?,
    @SerialName("supportHours")
    val supportHours: String?,
    @SerialName("supportPhone")
    val supportPhone: String?,
    @SerialName("themColor")
    val themColor: String?,
    @SerialName("webSite")
    val webSite: String?,
) : NetworkResponse

@Serializable
data class SchoolSlider(
    @SerialName("description")
    val description: String? = null,
    @SerialName("imgPath")
    val imgPath: String?,
    @SerialName("module")
    val module: String
)


