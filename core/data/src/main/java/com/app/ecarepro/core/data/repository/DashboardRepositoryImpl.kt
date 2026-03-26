package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.dashboard.AdmissionComparison
import com.app.ecarepro.core.domain.model.dashboard.AdmissionStandard
import com.app.ecarepro.core.domain.model.dashboard.BankBalance
import com.app.ecarepro.core.domain.model.dashboard.BirthdaySummary
import com.app.ecarepro.core.domain.model.dashboard.ClassSummary
import com.app.ecarepro.core.domain.model.dashboard.CollectionMode
import com.app.ecarepro.core.domain.model.dashboard.DashFeedItem
import com.app.ecarepro.core.domain.model.dashboard.DashboardActivity
import com.app.ecarepro.core.domain.model.dashboard.DashboardCard
import com.app.ecarepro.core.domain.model.dashboard.DashboardQuestionnaire
import com.app.ecarepro.core.domain.model.dashboard.DashboardResponse
import com.app.ecarepro.core.domain.model.dashboard.DashboardVisibility
import com.app.ecarepro.core.domain.model.dashboard.FeeCollectionData
import com.app.ecarepro.core.domain.model.dashboard.FeeDefaulterSummary
import com.app.ecarepro.core.domain.model.dashboard.LibraryData
import com.app.ecarepro.core.domain.model.dashboard.ModeWiseData
import com.app.ecarepro.core.domain.model.dashboard.StaffAttendance
import com.app.ecarepro.core.domain.model.dashboard.StatItem
import com.app.ecarepro.core.domain.model.dashboard.StudentBirthday
import com.app.ecarepro.core.domain.model.dashboard.TimetablePeriod
import com.app.ecarepro.core.domain.repository.DashboardRepository
import com.app.ecarepro.core.network.DashboardRemoteDataSource
import com.app.ecarepro.core.network.model.dashboard.NetworkDashboardCard
import com.app.ecarepro.core.network.model.dashboard.NetworkDashboardResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class DashboardRepositoryImpl @Inject constructor(
    private val dataSource: DashboardRemoteDataSource,
) : DashboardRepository {

    override fun getDashboard(): Flow<Result<DashboardResponse>> = asResultFlow {
        dataSource.getDashboard().toDomain()
    }

    override fun getFeeCollection(feeTypeId: Int, fromDate: String, tillDate: String): Flow<Result<FeeCollectionData>> = asResultFlow {
        val r = dataSource.getFeeCollection(feeTypeId, fromDate, tillDate)
        FeeCollectionData(
            estimate = r.estimate.orEmpty(),
            received = r.received.orEmpty(),
            concession = r.concession.orEmpty(),
            due = r.due.orEmpty(),
        )
    }

    override fun getModeWiseCollection(collectionDate: String): Flow<Result<ModeWiseData>> = asResultFlow {
        val r = dataSource.getModeWiseCollection(collectionDate)
        ModeWiseData(
            totalCollection = r.totalCollection ?: 0.0,
            modes = r.transactionDetails?.map {
                CollectionMode(mode = it.mode.orEmpty(), amount = it.amount ?: 0.0, color = it.color.orEmpty())
            } ?: emptyList(),
        )
    }

    override fun getFeed(): Flow<Result<List<DashFeedItem>>> = asResultFlow {
        dataSource.getFeed().updates?.map {
            DashFeedItem(
                menuID = it.menuID,
                chMenuID = it.chMenuID,
                sbChMenuID = it.sbChMenuID,
                module = it.module.orEmpty(),
                id = it.id.orEmpty(),
                caption = it.caption.orEmpty(),
                updatedOn = it.updtedOn.orEmpty(),
            )
        } ?: emptyList()
    }
}

private fun NetworkDashboardResponse.toDomain() = DashboardResponse(
    visibility = DashboardVisibility(
        showProgressStats = showProgressStats ?: false,
        showProCards = showProCards ?: false,
        showCards = showCards ?: false,
        showFeeDafaulter = showFeeDafaulter ?: false,
        showBDayCards = showBDayCards ?: false,
        showStuCategoryStatistics = showStuCategoryStatistics ?: false,
        showAttendanceSummary = showAttendanceSummery ?: false,
        showFeed = showFeed ?: false,
        showStuReligionWiseStatistics = showStuReligionWiseStatistics ?: false,
        showLibraryDTL = showLibraryDTL ?: false,
        showStaffAttendanceSummary = showStfAttendanceSummary ?: false,
        showBankBalance = showBankBalnce ?: false,
        showTeacherTimetable = showTeacherTimetable ?: false,
        showFeeCollection = showFeeCollection ?: false,
        showAdmissionComparison = showAdmissionComparison ?: false,
        showAdmissionModeComparison = showAdmissionModeComparison ?: false,
        showClassTimetable = showClassTimetable ?: false,
        showStuStatusWiseStatistics = showStuStatusWiseStatistics ?: false,
        showActivities = showActivities ?: false,
        showQuestionnaire = showQuestionnaire ?: false,
        showCollectionModeWise = showCollectionModeWise ?: false,
        showStudentBDayCards = showstudentBDayCards ?: false,
        showTeacherWorkLoad = showTeacherWorkLoad ?: false,
        showLibraryFineStatus = showLibraryFineStatus ?: false,
    ),
    proCards = proCards?.map { it.toDomain() } ?: emptyList(),
    cards = cards?.map { it.toDomain() } ?: emptyList(),
    activities = upcomingActivities?.map {
        DashboardActivity(
            id = it.id ?: 0,
            title = it.title.orEmpty(),
            isWorking = it.isWorking ?: true,
            fromDate = it.fromDate.orEmpty(),
            duration = it.duration ?: 1,
        )
    } ?: emptyList(),
    questionnaires = questionnaire?.map {
        DashboardQuestionnaire(
            qid = it.qid ?: 0,
            question = it.que.orEmpty(),
            likes = it.likes ?: 0,
            totalAnswer = it.totalAnswer ?: 0,
            photo = it.photo.orEmpty(),
            updatedOn = it.updatedOn.orEmpty(),
            updatedBy = it.updatedBy.orEmpty(),
            isAnswered = it.isAnswered ?: false,
            isVerified = it.isVerified ?: false,
        )
    } ?: emptyList(),
    studentBirthdays = studBirthdayCards?.map {
        StudentBirthday(
            studentName = it.studentName.orEmpty(),
            birthdayOn = it.birthdayOn.orEmpty(),
            photo = it.photo.orEmpty(),
            isToday = it.isToday ?: false,
        )
    } ?: emptyList(),
    birthdaySummaries = birthDayCards?.map {
        BirthdaySummary(heading = it.heading.orEmpty(), data = it.data.orEmpty())
    } ?: emptyList(),
    timetablePeriods = classTimetable?.map {
        TimetablePeriod(
            period = it.period ?: 0,
            subject = it.subject.orEmpty(),
            time = it.time.orEmpty(),
            teachBy = it.teachBy.orEmpty(),
            bookCover = it.bookCover.orEmpty(),
        )
    } ?: emptyList(),
    classSummaries = attendanceSummury?.classSummary?.map {
        ClassSummary(
            className = it.className.orEmpty(),
            present = it.present ?: 0,
            absent = it.absent ?: 0,
            leave = it.leave ?: 0,
            late = it.late ?: 0,
        )
    } ?: emptyList(),
    staffAttendance = staffAttendanceSummary?.let {
        StaffAttendance(
            present = it.present ?: 0,
            absent = it.absent ?: 0,
            onLeave = it.onLeave ?: 0,
            total = it.total ?: 0,
        )
    },
    bankBalances = bankBalance?.map {
        BankBalance(accountName = it.accountName.orEmpty(), balance = it.balnce.orEmpty())
    } ?: emptyList(),
    feeDefaulter = feeDafaulter?.let {
        FeeDefaulterSummary(
            totalStudent = it.totalStudent ?: 0,
            defaulterCount = it.defaulterCount ?: 0,
            amount = it.amount.orEmpty(),
        )
    },
    library = libraryDTL?.let {
        LibraryData(
            dueFine = it.dueFine ?: 0,
            fineCollected = it.fineCollected ?: 0,
            totalBooks = it.totalBooks ?: 0,
            circulatedBooks = it.circulatedBooks ?: 0,
            discardedBooks = it.discardedBooks ?: 0,
            newsSubscribed = it.newsSubscribed ?: 0,
            magazineSubscribed = it.magzineSubscribed ?: 0,
        )
    },
    admissionComparison = admissionComparison?.let {
        AdmissionComparison(
            previousSession = it.previousSession.orEmpty(),
            currentSession = it.currentSession.orEmpty(),
            nextSession = it.nextSession.orEmpty(),
            isNextSessionActive = it.isNextSessionActive ?: false,
            standards = it.studentCountStandardWise?.map { s ->
                AdmissionStandard(
                    standard = s.standard.orEmpty(),
                    previousSession = s.previousSession ?: 0,
                    currentSession = s.currentSession ?: 0,
                    nextSession = s.nextSession ?: 0,
                )
            } ?: emptyList(),
        )
    },
    stuStatusStats = stuStatusWiseStatistics?.map { StatItem(it.data.orEmpty(), it.value ?: 0) } ?: emptyList(),
    stuCategoryStats = stuCategoryWiseStatistics?.map { StatItem(it.data.orEmpty(), it.value ?: 0) } ?: emptyList(),
    stuReligionStats = stuReligionWiseStatistics?.map { StatItem(it.data.orEmpty(), it.value ?: 0) } ?: emptyList(),
    admissionModeStats = admissionModeComparison?.map { StatItem(it.data.orEmpty(), it.value ?: 0) } ?: emptyList(),
    sessionStartDate = sessionStartDate.orEmpty(),
    sessionEndDate = sessionEndDate.orEmpty(),
)

private fun NetworkDashboardCard.toDomain() = DashboardCard(
    heading = heading.orEmpty(),
    data = data.orEmpty(),
    color = color.orEmpty(),
    tinyIcon = tinyIcon.orEmpty(),
    menuID = menuID,
    chMenuID = chMenuID,
    sbChMenuID = sbChMenuID,
    value1 = value1,
    value2 = value2,
    data1 = data1,
    data2 = data2,
)
