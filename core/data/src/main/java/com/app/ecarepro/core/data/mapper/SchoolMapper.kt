package com.app.ecarepro.core.data.mapper

import com.app.ecarepro.core.database.model.SchoolEntity
import com.app.ecarepro.core.domain.model.School
import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.core.network.model.school.NetworkSchool
import com.app.ecarepro.core.network.model.school.NetworkSchoolDetails

fun NetworkSchool.toDomainModel() = School(
    schoolCode = schoolCode,
    name = name,
    address = address,
    city = city,
    logo = logo,
    state = state
)



/**
 * Converts the network model [NetworkSchoolDetails] to the database model [SchoolEntity].
 */
fun NetworkSchoolDetails.asEntity(): SchoolEntity {
    return SchoolEntity(
        schoolCode = requireNotNull(schoolCode) { "School code cannot be null to create a SchoolEntity" },
        active = this.active,
        assessmentMarksURL = this.assessmentMarksURL,
        city = this.city,
        contactEmail = this.contactEmail,
        eCareProSch = this.eCareProSch,
        feePaymentURL = this.feePayemtURL,
        feeReportURL = this.feeReportURL,
        isBoardingSchool = this.isBoardingSchool,
        isStudentLoginBlocked = this.isStudentLoginBlocked,
        logo = this.logo,
        logoNScName = this.logoNScName,
        logoScName = this.logoScName,
        marksEntryURL = this.marksEntryURL,
        schAdd1 = this.schAdd1,
        schAdd2 = this.schAdd2,
        schUpdatedOn = this.schUpdatedOn,
        schoolName = this.schoolName,
        state = this.state,
        supportDays = this.supportDays,
        supportEmail = this.supportEmail,
        supportHours = this.supportHours,
        supportPhone = this.supportPhone,
        themColor = this.themColor,
        webSite = this.webSite
    )
}

fun SchoolEntity.toDomainModel(): SchoolDetail {
    return SchoolDetail(
        schoolCode = schoolCode,
        active = this.active,
        assessmentMarksURL = this.assessmentMarksURL,
        city = this.city,
        contactEmail = this.contactEmail,
        eCareProSch = this.eCareProSch,
        feePaymentURL = this.feePaymentURL,
        feeReportURL = this.feeReportURL,
        isBoardingSchool = this.isBoardingSchool,
        isStudentLoginBlocked = this.isStudentLoginBlocked,
        logo = this.logo,
        logoNScName = this.logoNScName,
        logoScName = this.logoScName,
        marksEntryURL = this.marksEntryURL,
        schAdd1 = this.schAdd1,
        schAdd2 = this.schAdd2,
        schUpdatedOn = this.schUpdatedOn,
        schoolName = this.schoolName,
        state = this.state,
        supportDays = this.supportDays,
        supportEmail = this.supportEmail,
        supportHours = this.supportHours,
        supportPhone = this.supportPhone,
        themColor = this.themColor,
        webSite = this.webSite
    )
}

