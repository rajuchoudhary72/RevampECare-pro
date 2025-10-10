package com.app.ecarepro.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey
    @ColumnInfo("school_code")
    val schoolCode: String,
    val active: Int?,
    @ColumnInfo("assessment_marks_url")
    val assessmentMarksURL: String?,
    val city: String?,
    @ColumnInfo("contact_email")
    val contactEmail: String?,
    @ColumnInfo("ecare_pro_sch")
    val eCareProSch: Boolean?,
    @ColumnInfo("fee_payment_url")
    val feePaymentURL: String?,
    @ColumnInfo("fee_report_url")
    val feeReportURL: String?,
    @ColumnInfo("is_boarding_school")
    val isBoardingSchool: Boolean?,
    @ColumnInfo("is_student_login_blocked")
    val isStudentLoginBlocked: Boolean?,
    val logo: String?,
    @ColumnInfo("logo_nsc_Name")
    val logoNScName: String?,
    @ColumnInfo("logo_sc_name")
    val logoScName: String?,
    @ColumnInfo("marks_entry_url")
    val marksEntryURL: String?,
    @ColumnInfo("sch_add_1")
    val schAdd1: String?,
    @ColumnInfo("sch_add_2")
    val schAdd2: String?,
    @ColumnInfo("sch_updated_on")
    val schUpdatedOn: String?,
    @ColumnInfo("schoolName")
    val schoolName: String?,
    @ColumnInfo("state")
    val state: String?,
    @ColumnInfo("support_days")
    val supportDays: String?,
    @ColumnInfo("support_email")
    val supportEmail: String?,
    @ColumnInfo("support_hours")
    val supportHours: String?,
    @ColumnInfo("support_phone")
    val supportPhone: String?,
    @ColumnInfo("them_color")
    val themColor: String?,
    @ColumnInfo("web_site")
    val webSite: String?,
)