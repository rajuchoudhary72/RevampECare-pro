package com.app.ecarepro.feature.testingmenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.assignment.navigation.AssignmentNavigationGraph
import com.app.ecarepro.feature.dashboard.navigation.DashboardNavigationGraph
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import com.app.ecarepro.feature.schoolcode.navigation.SchoolCodeNavigationGraph
import com.app.ecarepro.feature.staffprofile.navigation.StaffProfileNavigationGraph
import com.app.ecarepro.feature.studentprofile.navigation.StudentProfileNavigationGraph
import com.app.ecarepro.feature.syllabus.navigation.SyllabusNavigationGraph
import com.app.ecarepro.feature.taskmanger.navigation.TaskMangerNavigationGraph
import com.app.ecarepro.feature.timetable.navigation.TimetableNavigationGraph
import com.app.ecarepro.feature.discipline.DisciplineUserType
import com.app.ecarepro.feature.discipline.navigation.DisciplineNavigationGraph
import com.app.ecarepro.feature.leave.navigation.LeaveNavigationGraph
import com.app.ecarepro.feature.announcement.navigation.AnnouncementNavGraph
import com.app.ecarepro.feature.announcement.notice.NoticeType
import com.app.ecarepro.feature.message.navigation.MessageNavigationGraph
import com.app.ecarepro.feature.transport_att.navigation.TransportAttNavigationGraph
import com.app.ecarepro.feature.gallery.navigation.GalleryNavGraph
import com.app.ecarepro.feature.update_record.navigation.UpdateRecordNavGraph
import com.app.ecarepro.feature.calendar.navigation.CalendarNavGraph
import com.app.ecarepro.feature.smsdailyconsumption.navigation.SmsDailyConsumptionNavGraph
import com.app.ecarepro.feature.survey.navigation.SurveyNavGraph
import com.app.ecarepro.feature.ebook.navigation.EBookNavGraph
import com.app.ecarepro.feature.fee.navigation.FeeNavGraph
import com.app.ecarepro.feature.feed.navigation.FeedNavGraph
import com.app.ecarepro.feature.library.navigation.LibraryNavGraph

import com.app.ecarepro.feature.report.navigation.BirthdayNavGraph
import com.app.ecarepro.feature.report.navigation.SMSReportNavGraph

import com.app.ecarepro.feature.conversationreport.navigation.ConversationReportNavGraph
import com.app.ecarepro.feature.globalsearch.navigation.GlobalSearchNavGraph
import com.app.ecarepro.feature.classteacher.navigation.ClassTeacherNavGraph
import com.app.ecarepro.feature.knowyourteacher.navigation.KnowYourTeacherNavGraph
import com.app.ecarepro.feature.setting.navigation.SettingsNavGraph
import com.app.ecarepro.feature.profile.navigation.MyProfileNavGraph
import com.app.ecarepro.feature.notice.navigation.NotificationsNavGraph
import com.app.ecarepro.feature.questionpaper.navigation.QuestionPaperNavGraph

import com.app.ecarepro.onboarding.feature.navigation.OnboardingNavigationGraph


object ModuleRegistry {
    val screens: Map<String, NavKey> = mapOf(
        "OnBoarding" to OnboardingNavigationGraph.Onboarding,
        "School Code" to SchoolCodeNavigationGraph.SchoolCode(),
        "Login" to LoginNavigationGraph.Login(schoolCode = "DEMOIN"),
        "Dashboard" to DashboardNavigationGraph.Dashboard,
        "Timetable" to TimetableNavigationGraph.Timetable,
        "Syllabus" to SyllabusNavigationGraph.Syllabus,
        "Assignment" to AssignmentNavigationGraph.Assignment,
        "Student Profile" to  StudentProfileNavigationGraph.StudentProfileList,
        "Staff Profile" to  StaffProfileNavigationGraph.StaffProfileList,
        "Applied Leaves (Self)" to LeaveNavigationGraph.AppliedLeavesSelf,
        "Applied Leaves (Student)" to LeaveNavigationGraph.AppliedLeavesStudent,
        "Applied Leaves (Staff)" to LeaveNavigationGraph.AppliedLeavesStaff,
        "Apply Leave (Student)" to LeaveNavigationGraph.ApplyLeaveStudent,
        "Apply Leave (Staff)" to LeaveNavigationGraph.ApplyLeaveStaff,
        "Task Manager" to  TaskMangerNavigationGraph.TaskMangerList,
        "Infraction List (Student)" to DisciplineNavigationGraph.InfractionList(DisciplineUserType.STUDENT),
        "Infraction List (Staff)" to DisciplineNavigationGraph.InfractionList(DisciplineUserType.STAFF),
        "Appreciation List" to DisciplineNavigationGraph.AppreciationList(),
        "Transport Attendance" to TransportAttNavigationGraph.TransportAttendance,
        "Message" to MessageNavigationGraph.MessageList,
        "School Notice" to AnnouncementNavGraph.NoticeList(NoticeType.SCHOOL.ordinal),
        "Staff Notice" to AnnouncementNavGraph.NoticeList(NoticeType.STAFF.ordinal),
        "Class Notice" to AnnouncementNavGraph.NoticeList(NoticeType.CLASS.ordinal),
        "Circular" to AnnouncementNavGraph.Circular,
        "Create Circular" to AnnouncementNavGraph.CreateCircular,
        "Gallery - Photo Albums" to GalleryNavGraph.PhotoAlbumList,
        "Gallery - Video Albums" to GalleryNavGraph.VideoAlbumList,
        "Gallery - Kids Corner" to GalleryNavGraph.KidsCornerList,
        "Gallery - Media Gallery" to GalleryNavGraph.MediaGalleryList,
        "Gallery - Favorites" to GalleryNavGraph.Favorites,
        "Update Record - Class Promotion" to UpdateRecordNavGraph.ClassPromotion,
        "Update Record - Manage Roll Number" to UpdateRecordNavGraph.ManageRollNumber,
        "Update Record - Update House" to UpdateRecordNavGraph.UpdateHouse,
        "Update Record - Update Profile Picture" to UpdateRecordNavGraph.UpdateProfilePicture,
        "Activity Calendar" to CalendarNavGraph.ActivityCalendarMain,
        "Survey" to SurveyNavGraph.SurveyList,
        "Library" to LibraryNavGraph.LibraryMain,
        "E-Books" to EBookNavGraph.EBookMain,
        "Feed" to FeedNavGraph.Feed,

        "Fee Receipt" to FeeNavGraph.ReceiptList,
        "Fee Collection Report" to FeeNavGraph.CollectionReport,
        "Fee Defaulter Report" to FeeNavGraph.ReportFilter("defaulter"),
        "Fee Estimate Report" to FeeNavGraph.ReportFilter("estimate"),
        "Fee Certificate" to FeeNavGraph.FeeCertificate,
        "SMS Report" to SMSReportNavGraph.Report,
        "SMS Daily Consumption (Report)" to SMSReportNavGraph.DailyConsumption,
        "Birthday Report" to BirthdayNavGraph.BirthdayList,
        "Settings" to SettingsNavGraph.SettingsMain,
        "Change Username" to SettingsNavGraph.ChangeUsername,
        "Change Password" to SettingsNavGraph.ChangePassword,
        "Change Language" to SettingsNavGraph.ChangeLanguage,

        "SMS Daily Consumption" to SmsDailyConsumptionNavGraph.SmsDailyConsumption,
        "Conversation Report" to ConversationReportNavGraph.ConversationList,
        "Global Search" to GlobalSearchNavGraph.GlobalSearch,
        "Know Your Teacher" to KnowYourTeacherNavGraph.KnowYourTeacherList,
        "Class Teacher" to ClassTeacherNavGraph.ClassTeacherList,
        "My Profile" to MyProfileNavGraph.Profile,
        "Notifications" to NotificationsNavGraph.NotificationList,
        "Question Paper" to QuestionPaperNavGraph.QuestionPaper,

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestingMenuScreen(
    navigateToBack: () -> Unit,
    navigateToModule: (NavKey) -> Unit,
) {

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = "Feature Modules",
                onNavigationClicked = navigateToBack,

                )
        },
        containerColor = MaterialTheme.appColors.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ModuleRegistry.screens.keys.toList()) { name ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigateToModule(ModuleRegistry.screens[name]!!)
                        }
                        .padding(4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

}


@Preview()
@Composable
private fun TestingMenuPreview() {
    EcareProTheme {
        TestingMenuScreen(
            navigateToBack = {},
            navigateToModule = {}
        )
    }
}