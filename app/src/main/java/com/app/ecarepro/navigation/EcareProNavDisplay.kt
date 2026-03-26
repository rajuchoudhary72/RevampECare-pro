package com.app.ecarepro.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.designsystem.core.component.DownloadFilesView
import com.app.ecarepro.designsystem.core.component.ECAttachment
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.assignment.navigation.AssignmentNavigationGraph
import com.app.ecarepro.feature.assignment.navigation.EntryAssignmentNavigation
import com.app.ecarepro.feature.dashboard.navigation.DashboardNavigationGraph
import com.app.ecarepro.feature.dashboard.navigation.EntryDashboardNavigation
import com.app.ecarepro.feature.docviewer.navigation.DocViewerNavigationGraph
import com.app.ecarepro.feature.docviewer.navigation.EntryDocViewerNavigation
import com.app.ecarepro.feature.login.navigation.EntryLoginNavigation
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import com.app.ecarepro.feature.schoolcode.navigation.EntrySchoolCodeNavigation
import com.app.ecarepro.feature.schoolcode.navigation.SchoolCodeNavigationGraph
import com.app.ecarepro.feature.splash.navigation.EntrySplashNavigation
import com.app.ecarepro.feature.splash.navigation.SplashNavigationGraph
import com.app.ecarepro.feature.staffprofile.navigation.EntryStaffProfileNavigation
import com.app.ecarepro.feature.staffprofile.navigation.StaffProfileNavigationGraph
import com.app.ecarepro.feature.studentprofile.navigation.EntryStudentProfileNavigation
import com.app.ecarepro.feature.studentprofile.navigation.StudentProfileNavigationGraph
import com.app.ecarepro.feature.syllabus.navigation.EntrySyllabusNavigation
import com.app.ecarepro.feature.syllabus.navigation.SyllabusNavigationGraph
import com.app.ecarepro.feature.taskmanger.navigation.EntryTaskMangerNavigation
import com.app.ecarepro.feature.taskmanger.navigation.TaskMangerNavigationGraph
import com.app.ecarepro.feature.testingmenu.navigation.EntryTestingMenuNavigation
import com.app.ecarepro.feature.testingmenu.navigation.TestingMenuNavigationGraph
import com.app.ecarepro.feature.timetable.navigation.EntryTimetableNavigation
import com.app.ecarepro.feature.timetable.navigation.TimetableNavigationGraph
import com.app.ecarepro.feature.discipline.navigation.DisciplineNavigationGraph
import com.app.ecarepro.feature.discipline.navigation.EntryDisciplineNavigation
import com.app.ecarepro.feature.discipline.DisciplineUserType
import com.app.ecarepro.feature.leave.navigation.EntryLeaveNavigation
import com.app.ecarepro.feature.leave.navigation.LeaveNavigationGraph
import com.app.ecarepro.feature.announcement.navigation.EntryAnnouncementNavigation
import com.app.ecarepro.feature.gallery.navigation.EntryGalleryNavigation
import com.app.ecarepro.feature.message.navigation.EntryMessageNavigation
import com.app.ecarepro.feature.update_record.navigation.entryUpdateRecordNavigation
import com.app.ecarepro.feature.transport_att.navigation.EntryTransportAttNavigation
import com.app.ecarepro.feature.transport_att.navigation.TransportAttNavigationGraph
import com.app.ecarepro.feature.calendar.navigation.CalendarNavGraph
import com.app.ecarepro.feature.calendar.navigation.EntryCalendarNavigation
import com.app.ecarepro.feature.smsdailyconsumption.navigation.entrySmsDailyConsumptionNavigation
import com.app.ecarepro.feature.conversationreport.navigation.ConversationReportNavGraph
import com.app.ecarepro.feature.conversationreport.navigation.entryConversationReportNavigation
import com.app.ecarepro.feature.globalsearch.navigation.GlobalSearchNavGraph
import com.app.ecarepro.feature.globalsearch.navigation.entryGlobalSearchNavigation
import com.app.ecarepro.feature.announcement.navigation.AnnouncementNavGraph
import com.app.ecarepro.feature.announcement.notice.NoticeType
import com.app.ecarepro.feature.gallery.navigation.GalleryNavGraph
import com.app.ecarepro.feature.message.navigation.MessageNavigationGraph
import com.app.ecarepro.feature.update_record.navigation.UpdateRecordNavGraph
import com.app.ecarepro.feature.library.navigation.LibraryNavGraph
import com.app.ecarepro.feature.ebook.navigation.EBookNavGraph
import com.app.ecarepro.feature.feed.navigation.FeedNavGraph
import com.app.ecarepro.feature.survey.navigation.SurveyNavGraph
import com.app.ecarepro.feature.survey.navigation.entrySurveyNavigation
import com.app.ecarepro.feature.ebook.navigation.entryEBookNavigation
import com.app.ecarepro.feature.fee.navigation.FeeNavGraph
import com.app.ecarepro.feature.fee.navigation.entryFeeNavigation
import com.app.ecarepro.feature.feed.navigation.entryFeedNavigation
import com.app.ecarepro.feature.library.navigation.entryLibraryNavigation

import com.app.ecarepro.feature.report.navigation.BirthdayNavGraph
import com.app.ecarepro.feature.report.navigation.entryBirthdayNavigation
import com.app.ecarepro.feature.report.navigation.entrySMSReportNavigation
import com.app.ecarepro.feature.classteacher.navigation.ClassTeacherNavGraph
import com.app.ecarepro.feature.classteacher.navigation.entryClassTeacherNavigation
import com.app.ecarepro.feature.knowyourteacher.navigation.KnowYourTeacherNavGraph
import com.app.ecarepro.feature.knowyourteacher.navigation.entryKnowYourTeacherNavigation
import com.app.ecarepro.feature.marksmanager.navigation.MarkManagerNavGraph
import com.app.ecarepro.feature.marksmanager.navigation.MarkManagerType
import com.app.ecarepro.feature.marksmanager.navigation.entryMarkManagerNavigation
import com.app.ecarepro.feature.setting.navigation.SettingsNavGraph
import com.app.ecarepro.feature.setting.navigation.entrySettingsNavigation
import com.app.ecarepro.feature.profile.navigation.MyProfileNavGraph
import com.app.ecarepro.feature.profile.navigation.entryMyProfileNavigation
import com.app.ecarepro.feature.notice.navigation.NotificationsNavGraph
import com.app.ecarepro.feature.notice.navigation.entryNotificationsNavigation
import com.app.ecarepro.feature.questionpaper.navigation.QuestionPaperNavGraph
import com.app.ecarepro.feature.questionpaper.navigation.entryQuestionPaperNavigation

import com.app.ecarepro.feature.smsdailyconsumption.navigation.SmsDailyConsumptionNavGraph

import com.app.ecarepro.feature.homeselection.navigation.HomeSelectionNavigationGraph
import com.app.ecarepro.feature.feed.FeedScreen
import com.app.ecarepro.feature.dashboard.home.HomeScreen
import com.app.ecarepro.core.domain.model.HomeScreenType
import com.app.ecarepro.onboarding.feature.navigation.EntryOnboardingNavigation
import com.app.ecarepro.onboarding.feature.navigation.OnboardingNavigationGraph
import kotlinx.serialization.Serializable

@Serializable
sealed interface AttachmentNavigationGraph : NavKey {
    @Serializable
    data class AttachmentList(
        val attachments: List<ECAttachment>
    ) : AttachmentNavigationGraph
}

@Composable
fun EcareProNavDisplay(
    startDestination: NavKey? = null,
    onRestartActivity: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val backStack = remember {
        val initialKey = startDestination ?: SplashNavigationGraph.Splash
        mutableStateListOf(initialKey)
    }

    val homeScreenTypeViewModel: HomeScreenTypeViewModel = hiltViewModel()
    val homeScreenType by homeScreenTypeViewModel.homeScreenType.collectAsStateWithLifecycle()

    // Helper functions for navigation
    fun navigateBack() {
        backStack.removeLastOrNull()
    }

    fun navigateToDocViewer(title: String, url: String) {
        backStack.add(
            DocViewerNavigationGraph.DocViewer(
                title = title,
                docUrl = url
            )
        )
    }

    fun navigateToAttachmentList(attachments: List<ECAttachment>) {
        backStack.add(
            AttachmentNavigationGraph.AttachmentList(
                attachments = attachments
            )
        )
    }

    fun resolveMenuNavKey(menuId: Int): NavKey? = when (menuId) {
        // Transport & Assignment & Syllabus & Timetable
        17 -> TransportAttNavigationGraph.TransportAttendance
        18 -> AssignmentNavigationGraph.Assignment
        19 -> SyllabusNavigationGraph.Syllabus
        24 -> TimetableNavigationGraph.Timetable
        // Task Manager
        28 -> TaskMangerNavigationGraph.TaskMangerList
        // Class Teacher
        66 -> ClassTeacherNavGraph.ClassTeacherList
        // Mark Manager
        22 -> MarkManagerNavGraph.MarkManager(MarkManagerType.MARKS_ENTRY)
        // School Website
       35 -> MarkManagerNavGraph.MarkManager(MarkManagerType.SCHOOL_WEBSITE)
        // Student Profile
        9, 57 -> StudentProfileNavigationGraph.StudentProfileList
        // Staff Profile
        13, 63 -> StaffProfileNavigationGraph.StaffProfileList
        // Leave
        59 -> LeaveNavigationGraph.AppliedLeavesStudent
        64 -> LeaveNavigationGraph.AppliedLeavesStaff
        94 -> LeaveNavigationGraph.AppliedLeavesSelf
        // Discipline
        10, 61, 110, 111 -> DisciplineNavigationGraph.AppreciationList()
        62, 114, 116, 117 -> DisciplineNavigationGraph.InfractionList(userType = DisciplineUserType.STUDENT)
        115 -> DisciplineNavigationGraph.InfractionList(userType = DisciplineUserType.STAFF)
        // Calendar
        26 -> CalendarNavGraph.ActivityCalendarMain


        14 -> BirthdayNavGraph.BirthdayList


        // Survey

        8 -> SurveyNavGraph.SurveyList
        // Message
        2, 44, 45, 46, 47 -> MessageNavigationGraph.MessageList
        // Announcement / Notice
        4, 52 -> AnnouncementNavGraph.NoticeList(NoticeType.SCHOOL.ordinal)
        53 -> AnnouncementNavGraph.NoticeList(NoticeType.CLASS.ordinal)
        54 -> AnnouncementNavGraph.NoticeList(NoticeType.STAFF.ordinal)
        55 -> AnnouncementNavGraph.Circular
        56 -> AnnouncementNavGraph.CreateCircular
        // Gallery
        34, 71 -> GalleryNavGraph.PhotoAlbumList
        72 -> GalleryNavGraph.VideoAlbumList
        73 -> GalleryNavGraph.Favorites
        74 -> GalleryNavGraph.MediaGalleryList
        75 -> GalleryNavGraph.KidsCornerList
        // Library & E-Books & Feed
        30 -> LibraryNavGraph.LibraryMain
        31 -> EBookNavGraph.EBookMain
        // Update Record
        60, 102 -> UpdateRecordNavGraph.ClassPromotion
        103 -> UpdateRecordNavGraph.ManageRollNumber
        104 -> UpdateRecordNavGraph.UpdateHouse
        105 -> UpdateRecordNavGraph.UpdateProfilePicture
        // SMS Daily Consumption
        85, 121, 122 -> SmsDailyConsumptionNavGraph.SmsDailyConsumption
        // Conversation Report
        81 -> ConversationReportNavGraph.ConversationList
        // Fee Certificate
        242 -> FeeNavGraph.FeeCertificate
        // Question Paper
        84 -> QuestionPaperNavGraph.QuestionPaper
        // Settings
        200 -> SettingsNavGraph.SettingsMain

        else -> null
    }

    fun navigateToAttachmentListFromUrls(attachmentUrls: List<String>) {
        val attachments = attachmentUrls.mapIndexed { index, url ->
            ECAttachment(
                name = url.substringAfterLast('/').ifEmpty { "Attachment ${index + 1}" },
                url = url
            )
        }
        navigateToAttachmentList(attachments)
    }

    NavDisplay(
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        entryProvider = entryProvider {

            EntryTestingMenuNavigation(
                navigateToBack = ::navigateBack,
                navigateToModule = { navKey ->
                    backStack.add(navKey)
                }
            )

            EntrySplashNavigation(
                navigateToLogin = {
                    backStack.clear()
                    backStack.add(OnboardingNavigationGraph.Onboarding)
                },
                navigateToDashboard = { user ->
                    backStack.clear()
                    backStack.add(DashboardNavigationGraph.Dashboard)
                }
            )

            EntryOnboardingNavigation(
                navigateToAddSchool = {
                    backStack.clear()
                    backStack.add(SchoolCodeNavigationGraph.SchoolCode())
                }
            )

            EntrySchoolCodeNavigation(
                backStack = backStack,
                navigateToLogin = { schoolCode, isAddAccount ->
                    backStack.add(LoginNavigationGraph.Login(schoolCode = schoolCode, isAddAccount = isAddAccount))
                }
            )

            EntryLoginNavigation(
                backStack = backStack,
                backToSchoolCode = ::navigateBack,
                navigateToMain = { user ->
                    homeScreenTypeViewModel.refresh()
                    backStack.clear()
                    backStack.add(DashboardNavigationGraph.Dashboard)
                },
                onAddAccountComplete = {
                    onRestartActivity()
                },
                onHomeSelectionComplete = {
                    homeScreenTypeViewModel.refresh()
                },
            )

            EntryDashboardNavigation(
                navigateToProfile = {
                    backStack.add(MyProfileNavGraph.Profile)
                },
                onMenuNavigate = { menuId ->
                    resolveMenuNavKey(menuId)?.let { backStack.add(it) }
                },
                navigateToGlobalSearch = {
                    backStack.add(GlobalSearchNavGraph.GlobalSearch)
                },
                navigateToNotifications = {
                    backStack.add(NotificationsNavGraph.NotificationList)
                },
                navigateToSettings = {
                    backStack.add(SettingsNavGraph.SettingsMain)
                },
                navigateToHomeSelection = {
                    backStack.add(HomeSelectionNavigationGraph.HomeSelection)
                },
                homeContent = {
                    when (homeScreenType) {
                        HomeScreenType.FEED_OR_TIMELINE -> FeedScreen()
                        else -> HomeScreen(
                            navigateToQuestionnaire = {},
                            navigateToNotifications = { backStack.add(NotificationsNavGraph.NotificationList) },
                            navigateToSettings = { backStack.add(SettingsNavGraph.SettingsMain) },
                            navigateToHomeSelection = { backStack.add(HomeSelectionNavigationGraph.HomeSelection) },
                        )
                    }
                },
            )

            EntryTimetableNavigation(
                navigateToBack = ::navigateBack
            )

            EntrySyllabusNavigation(
                backStack = backStack,
                navigateToBack = ::navigateBack,
                navigateToAttachmentList = ::navigateToAttachmentList
            )

            EntryStudentProfileNavigation(
                backStack = backStack,
                navigateToBack = ::navigateBack,
                navigateToProfileDetail = { profile ->
                    // TODO: Navigate to student profile detail screen when implemented
                    // For now, this is a placeholder
                },
                navigateToDocViewer = ::navigateToDocViewer
            )

            EntryStaffProfileNavigation(
                backStack = backStack,
                navigateToBack = ::navigateBack,
                navigateToProfileDetail = { profile ->
                    // TODO: Navigate to staff profile detail screen when implemented
                    // For now, this is a placeholder
                }
            )
            EntryTaskMangerNavigation (
                backStack = backStack,
                navigateToBack = ::navigateBack,
                navigateToDocViewer = ::navigateToDocViewer,
            )

            EntryDocViewerNavigation(
                navigateToBack = ::navigateBack
            )

            entry<AttachmentNavigationGraph.AttachmentList> { navEntry ->
                val viewModel: AttachmentListViewModel = hiltViewModel()
                val snackbarHostState = remember { SnackbarHostState() }
                var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }
                val downloadedFiles by viewModel.downloadedFiles.collectAsStateWithLifecycle()

                LaunchedEffect(navEntry.attachments) {
                    viewModel.checkDownloadedFiles(navEntry.attachments)
                }

                LaunchedEffect(Unit) {
                    viewModel.messageEvent.collect { message ->
                        snackbarMessage = message
                        snackbarHostState.showSnackbar(message.text)
                    }
                }

                EcareProScaffold(
                    containerColor = androidx.compose.ui.graphics.Color.White,
                    snackbarHostState = snackbarHostState,
                    snackbarMessage = snackbarMessage,
                    onSnackbarDismissed = { snackbarMessage = null }
                ) {
                    DownloadFilesView(
                        attachments = navEntry.attachments,
                        onBackPressed = ::navigateBack,
                        onAttachmentClick = { attachment ->
                            navigateToDocViewer(attachment.name, attachment.url)
                        },
                        onDownloadClick = { attachment ->
                            if (attachment.name in downloadedFiles) {
                                val path = viewModel.getDownloadedFilePath(attachment.name)
                                backStack.add(
                                    DocViewerNavigationGraph.DocViewer(
                                        title = attachment.name,
                                        docUrl = path
                                    )
                                )
                            } else {
                                viewModel.downloadAttachment(attachment)
                            }
                        },
                        downloadedFileNames = downloadedFiles
                    )
                }
            }

            EntryAssignmentNavigation(
                backStack = backStack,
                navigateToBack = ::navigateBack,
                openDocVier = ::navigateToDocViewer,
                openAttachments = { _, attachmentUrls ->
                    navigateToAttachmentListFromUrls(attachmentUrls)
                }
            )

            EntryLeaveNavigation(
                onBackClick = {
                    backStack.removeLastOrNull()
                },
                onApplyLeaveClick = {}
            )

            EntryDisciplineNavigation(
                backStack = backStack,
                navigateToBack = {
                    backStack.removeLastOrNull()
                },
            )

            EntryMessageNavigation(
                backStack = backStack,
                navigateToBack = ::navigateBack,
            )

            EntryTransportAttNavigation(
                backStack = backStack,
                navigateToBack = {
                    backStack.removeLastOrNull()
                },
            )

            EntryAnnouncementNavigation(
                backStack = backStack,
                navigateBack = ::navigateBack,
                navigateToAttachmentList = ::navigateToAttachmentList,
            )

            EntryGalleryNavigation(
                backStack = backStack,
                navigateBack = ::navigateBack,
            )

            entryUpdateRecordNavigation(
                navigateBack = ::navigateBack,
            )

            EntryCalendarNavigation(
                navigateToBack = ::navigateBack,
            )

            entryLibraryNavigation(
                navigateBack = ::navigateBack,
                navigateTo = { backStack.add(it) },
            )

            entryEBookNavigation(
                navigateBack = ::navigateBack,
                navigateToPdf = { title, url -> navigateToDocViewer(title, url) },
            )

            entryFeedNavigation(
                navigateBack = ::navigateBack,
            )


            entryFeeNavigation(
                navigateBack = ::navigateBack,
                navigateTo = { backStack.add(it) },
            )

            entrySurveyNavigation(
                navigateBack = ::navigateBack,
                navigateTo = { backStack.add(it) },
            )

            entrySMSReportNavigation(
                navigateBack = ::navigateBack,
            )

            entryBirthdayNavigation(
                navigateBack = ::navigateBack,
            )

            entrySmsDailyConsumptionNavigation(
                navigateBack = ::navigateBack,
            )

            entryConversationReportNavigation(
                navigateBack = ::navigateBack,
                navigateTo = { backStack.add(it) },
                navigateToAttachment = { title, url -> navigateToDocViewer(title, url) },
            )

            entryGlobalSearchNavigation(
                navigateBack = ::navigateBack,
                navigateToStudentProfile = { studentId ->
                    backStack.add(StudentProfileNavigationGraph.StudentProfileList)
                },
                navigateToModule = { menuId ->
                    resolveMenuNavKey(menuId)?.let { backStack.add(it) }
                },

            )

            entryMarkManagerNavigation(
                navigateBack = ::navigateBack,
            )

            entryKnowYourTeacherNavigation(
                navigateToBack = ::navigateBack,
            )

            entryClassTeacherNavigation(
                navigateToBack = ::navigateBack,
            )

            entrySettingsNavigation(
                navigateBack = ::navigateBack,
                navigateTo = { backStack.add(it) },
                onLogout = {
                    backStack.clear()
                    backStack.add(OnboardingNavigationGraph.Onboarding)
                },
                onRestartActivity = onRestartActivity,
            )

            entryMyProfileNavigation(
                navigateBack = ::navigateBack,
                navigateTo = { backStack.add(it) },
                navigateToLogin = { onLogout() },
                navigateToAddAccount = {
                    backStack.add(SchoolCodeNavigationGraph.SchoolCode(isAddAccount = true))
                },
                onRestartApp = { onRestartActivity() },
            )

            entryNotificationsNavigation(
                navigateBack = ::navigateBack,
                navigateTo = { backStack.add(it) },
            )

            entryQuestionPaperNavigation(
                navigateBack = ::navigateBack,
            )
        }
    )
}