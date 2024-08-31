package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.FeeSummery
import com.app.ecarepro.model.Library
import com.app.ecarepro.model.MedicalCard
import com.app.ecarepro.model.MedicineIssued
import com.app.ecarepro.model.Profile
import com.app.ecarepro.model.ProfileAttendanceDTL
import com.app.ecarepro.model.RecentAppreciation
import com.app.ecarepro.model.RecentInfraction
import com.app.ecarepro.model.SiblingDetails
import com.app.ecarepro.model.TransDetails

data class NetworkStudentProfile(
    val academicYears: List<AcademicYear>,
    val assignments: Int,
    val attendanceDTL: ProfileAttendanceDTL,
    val attendancePer: String,
    val bookIssued: Int,
    val errorCode: Int,
    val feeDues: String,
    val feeSummery: FeeSummery,
    val recentInfractions: List<RecentInfraction>,
    val recentAppreciations: List<RecentAppreciation>,
    val medicalCard: MedicalCard,

    val id: String,
    val infirmaryVisits: Int,
    val infractions: Int,
    val lastExamResult: String,
    val leaveRecord: String,
    val library: Library,
    val medicineIssued: List<MedicineIssued>,
    val siblingDetails: List<SiblingDetails>,
    val message: String,
    val profile: Profile,
    val sectionControl: SectionControl,
    val status: String,
    val transDetails: TransDetails
)