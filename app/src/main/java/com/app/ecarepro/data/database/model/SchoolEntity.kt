package com.app.ecarepro.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.Slider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey val schoolCode: String,
    val active: Int?,
    val assessmentMarksURL: String?,
    val city: String?,
    val contactEmail: String?,
    val eCareProSch: Boolean?,
    val feePaymentURL: String?,
    val feeReportURL: String?,
    val isBoardingSchool: Boolean?,
    val logo: String?,
    val logoNScName: String?,
    val logoScName: String?,
    val marksEntryURL: String?,
    val schAdd1: String?,
    val schAdd2: String?,
    val schUpdatedOn: String?,
    val schoolName: String?,
    val state: String?,
    val supportDays: String?,
    val supportEmail: String?,
    val supportHours: String?,
    val supportPhone: String?,
    val themColor: String?,
    val webSite: String?,
    val slides: String?,
)

fun SchoolEntity.asNetworkSchool(): NetworkSchool {
    return NetworkSchool(
        schoolCode = schoolCode,
        active = active,
        assessmentMarksURL = assessmentMarksURL,
        city = city,
        contactEmail = contactEmail,
        eCareProSch = eCareProSch,
        feePayemtURL = feePaymentURL,
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
        slider = Gson().fromJson(slides, object : TypeToken<List<Slider>>() {}.type),
        status = "0",
        message = "",
        errorCode = 0,
    )
}