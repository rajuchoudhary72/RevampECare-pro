package com.app.ecarepro.feature.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * RouteType - Type-safe navigation destinations
 *
 * All app screens are defined here as sealed class variants.
 * This ensures compile-time safety and prevents navigation errors.
 *
 * Example usage:
 * ```kotlin
 * // Navigate to assignment list
 * NavigationManager.navigateToRoute(RouteType.Assignment)
 *
 * // Navigate to assignment detail with data
 * NavigationManager.navigateToRoute(
 *     RouteType.AssignmentDetail(presentation)
 * )
 * ```
 */
sealed class RouteType : Parcelable {

    // MARK: - Core Navigation

    @Parcelize
    object Home : RouteType()

    @Parcelize
    object Dashboard : RouteType()

    @Parcelize
    object Profile : RouteType()

    // MARK: - Authentication

    @Parcelize
    object SchoolCode : RouteType()

    @Parcelize
    data class Login(val schoolCode: String) : RouteType()

    @Parcelize
    data class LoginOTP(
        val userName: String,
        val password: String,
        val twoFactorData: String? = null
    ) : RouteType()

    @Parcelize
    object ForgotPassword : RouteType()

    @Parcelize
    object Help : RouteType()

    // MARK: - Assignment Module

    @Parcelize
    object Assignment : RouteType()

    @Parcelize
    data class AssignmentDetail(val assignmentId: Int) : RouteType()

    @Parcelize
    data class AddAssignment(val assignmentId: Int? = null) : RouteType()

    @Parcelize
    data class EditAssignment(val assignmentId: Int) : RouteType()

    @Parcelize
    data class SubmitAssignment(val assignmentId: Int) : RouteType()

    // MARK: - Timetable Module

    @Parcelize
    object Timetable : RouteType()

    @Parcelize
    data class TimetableDetail(val classId: Int) : RouteType()

    // MARK: - Questionnaire Module

    @Parcelize
    object Questionnaire : RouteType()

    @Parcelize
    object AddQuestion : RouteType()

    @Parcelize
    data class QuestionnaireDetail(val questionnaireId: Int) : RouteType()

    @Parcelize
    data class AnswerQuestionnaire(val questionnaireId: Int) : RouteType()

    // MARK: - Syllabus Module

    @Parcelize
    object SyllabusList : RouteType()

    @Parcelize
    data class SyllabusDetails(val syllabusId: Int) : RouteType()

    @Parcelize
    object AddSyllabus : RouteType()

    @Parcelize
    data class EditSyllabus(val syllabusId: Int) : RouteType()

    // MARK: - Leave Module

    @Parcelize
    object LeaveList : RouteType()

    @Parcelize
    object LeaveReport : RouteType()

    @Parcelize
    data class LeaveDetail(val leaveId: Int) : RouteType()

    @Parcelize
    data class ApplyLeave(val leaveId: Int? = null) : RouteType()

    // MARK: - Message Module

    @Parcelize
    object MessageList : RouteType()

    @Parcelize
    object ComposeMessage : RouteType()

    @Parcelize
    data class MessageThread(val threadId: String) : RouteType()

    @Parcelize
    data class MessageDetail(val messageId: String) : RouteType()

    // MARK: - Notification Module

    @Parcelize
    object NotificationList : RouteType()

    @Parcelize
    data class NotificationDetail(val notificationId: Int) : RouteType()

    // MARK: - Student Module

    @Parcelize
    object StudentList : RouteType()

    @Parcelize
    data class StudentProfile(val studentId: Int) : RouteType()

    @Parcelize
    data class StudentAttendance(val studentId: Int) : RouteType()

    @Parcelize
    data class StudentReportCard(val studentId: Int) : RouteType()

    // MARK: - Staff Module

    @Parcelize
    object StaffList : RouteType()

    @Parcelize
    data class StaffProfile(val staffId: Int) : RouteType()

    @Parcelize
    data class StaffAttendance(val staffId: Int) : RouteType()

    // MARK: - Fee Module

    @Parcelize
    object FeePayment : RouteType()

    @Parcelize
    object FeeReceipt : RouteType()

    @Parcelize
    data class FeeDetail(val feeId: Int) : RouteType()

    // MARK: - Gallery Module

    @Parcelize
    object PhotoGallery : RouteType()

    @Parcelize
    object VideoGallery : RouteType()

    @Parcelize
    data class AlbumDetail(val albumId: Int) : RouteType()

    // MARK: - Settings Module

    @Parcelize
    object Settings : RouteType()

    @Parcelize
    object ChangePassword : RouteType()

    @Parcelize
    object EditProfile : RouteType()

    @Parcelize
    object About : RouteType()

    // MARK: - Common

    @Parcelize
    data class WebView(val url: String, val title: String) : RouteType()

    @Parcelize
    data class PdfViewer(val url: String, val title: String) : RouteType()

    @Parcelize
    data class ImageViewer(val imageUrl: String) : RouteType()

    // MARK: - Helper Methods

    /**
     * Get a unique identifier for this route (for analytics/logging)
     */
    fun getRouteId(): String {
        return when (this) {
            is Home -> "home"
            is Dashboard -> "dashboard"
            is Profile -> "profile"
            is SchoolCode -> "school_code"
            is Login -> "login"
            is LoginOTP -> "login_otp"
            is ForgotPassword -> "forgot_password"
            is Help -> "help"
            is Assignment -> "assignment"
            is AssignmentDetail -> "assignment_detail_$assignmentId"
            is AddAssignment -> "add_assignment"
            is EditAssignment -> "edit_assignment_$assignmentId"
            is SubmitAssignment -> "submit_assignment_$assignmentId"
            is Timetable -> "timetable"
            is TimetableDetail -> "timetable_detail_$classId"
            is Questionnaire -> "questionnaire"
            is AddQuestion -> "add_question"
            is QuestionnaireDetail -> "questionnaire_detail_$questionnaireId"
            is AnswerQuestionnaire -> "answer_questionnaire_$questionnaireId"
            is SyllabusList -> "syllabus_list"
            is SyllabusDetails -> "syllabus_details_$syllabusId"
            is AddSyllabus -> "add_syllabus"
            is EditSyllabus -> "edit_syllabus_$syllabusId"
            is LeaveList -> "leave_list"
            is LeaveReport -> "leave_report"
            is LeaveDetail -> "leave_detail_$leaveId"
            is ApplyLeave -> "apply_leave"
            is MessageList -> "message_list"
            is ComposeMessage -> "compose_message"
            is MessageThread -> "message_thread_$threadId"
            is MessageDetail -> "message_detail_$messageId"
            is NotificationList -> "notification_list"
            is NotificationDetail -> "notification_detail_$notificationId"
            is StudentList -> "student_list"
            is StudentProfile -> "student_profile_$studentId"
            is StudentAttendance -> "student_attendance_$studentId"
            is StudentReportCard -> "student_report_card_$studentId"
            is StaffList -> "staff_list"
            is StaffProfile -> "staff_profile_$staffId"
            is StaffAttendance -> "staff_attendance_$staffId"
            is FeePayment -> "fee_payment"
            is FeeReceipt -> "fee_receipt"
            is FeeDetail -> "fee_detail_$feeId"
            is PhotoGallery -> "photo_gallery"
            is VideoGallery -> "video_gallery"
            is AlbumDetail -> "album_detail_$albumId"
            is Settings -> "settings"
            is ChangePassword -> "change_password"
            is EditProfile -> "edit_profile"
            is About -> "about"
            is WebView -> "webview"
            is PdfViewer -> "pdf_viewer"
            is ImageViewer -> "image_viewer"
        }
    }

    /**
     * Get display name for this route (for UI/analytics)
     */
    fun getDisplayName(): String {
        return when (this) {
            is Home -> "Home"
            is Dashboard -> "Dashboard"
            is Profile -> "Profile"
            is SchoolCode -> "School Code"
            is Login -> "Login"
            is LoginOTP -> "Login OTP"
            is ForgotPassword -> "Forgot Password"
            is Help -> "Help"
            is Assignment -> "Assignments"
            is AssignmentDetail -> "Assignment Detail"
            is AddAssignment -> "Add Assignment"
            is EditAssignment -> "Edit Assignment"
            is SubmitAssignment -> "Submit Assignment"
            is Timetable -> "Timetable"
            is TimetableDetail -> "Timetable Detail"
            is Questionnaire -> "Questionnaire"
            is AddQuestion -> "Add Question"
            is QuestionnaireDetail -> "Questionnaire Detail"
            is AnswerQuestionnaire -> "Answer Questionnaire"
            is SyllabusList -> "Syllabus"
            is SyllabusDetails -> "Syllabus Details"
            is AddSyllabus -> "Add Syllabus"
            is EditSyllabus -> "Edit Syllabus"
            is LeaveList -> "Leave List"
            is LeaveReport -> "Leave Report"
            is LeaveDetail -> "Leave Detail"
            is ApplyLeave -> "Apply Leave"
            is MessageList -> "Messages"
            is ComposeMessage -> "Compose Message"
            is MessageThread -> "Message Thread"
            is MessageDetail -> "Message Detail"
            is NotificationList -> "Notifications"
            is NotificationDetail -> "Notification Detail"
            is StudentList -> "Students"
            is StudentProfile -> "Student Profile"
            is StudentAttendance -> "Student Attendance"
            is StudentReportCard -> "Report Card"
            is StaffList -> "Staff"
            is StaffProfile -> "Staff Profile"
            is StaffAttendance -> "Staff Attendance"
            is FeePayment -> "Fee Payment"
            is FeeReceipt -> "Fee Receipt"
            is FeeDetail -> "Fee Detail"
            is PhotoGallery -> "Photo Gallery"
            is VideoGallery -> "Video Gallery"
            is AlbumDetail -> "Album"
            is Settings -> "Settings"
            is ChangePassword -> "Change Password"
            is EditProfile -> "Edit Profile"
            is About -> "About"
            is WebView -> title
            is PdfViewer -> title
            is ImageViewer -> "Image Viewer"
        }
    }
}
