package com.app.ecarepro.data.network.service

import com.app.ecarepro.AssignHouseRequest
import com.app.ecarepro.data.network.model.AddThoughtsPostData
import com.app.ecarepro.data.network.model.ChangeUserNameRequestDto
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.GetCredentialsRequest
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkAddAppreciation
import com.app.ecarepro.data.network.model.NetworkAddInfraction
import com.app.ecarepro.data.network.model.NetworkAnswerDetails
import com.app.ecarepro.data.network.model.NetworkAppreciationInstance
import com.app.ecarepro.data.network.model.NetworkAssignments
import com.app.ecarepro.data.network.model.NetworkAttedanceSummary
import com.app.ecarepro.data.network.model.NetworkBirthday
import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkClassSyllabus
import com.app.ecarepro.data.network.model.NetworkClassAttendance
import com.app.ecarepro.data.network.model.NetworkCreateLesson
import com.app.ecarepro.data.network.model.NetworkInfractionInstance
import com.app.ecarepro.data.network.model.NetworkInfractionTypes
import com.app.ecarepro.data.network.model.NetworkLeaveListStatus
import com.app.ecarepro.data.network.model.NetworkLeaveSetting
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkLessonPlanDTL
import com.app.ecarepro.data.network.model.NetworkLessonPlanList
import com.app.ecarepro.data.network.model.NetworkMarkAttendance
import com.app.ecarepro.data.network.model.NetworkMySubjects
import com.app.ecarepro.data.network.model.NetworkPaySlip
import com.app.ecarepro.data.network.model.NetworkQuestionPaper
import com.app.ecarepro.data.network.model.NetworkQuestionnaire
import com.app.ecarepro.data.network.model.NetworkReportCardDetails
import com.app.ecarepro.data.network.model.NetworkStaffAttendence
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.data.network.model.NetworkStaffProfile
import com.app.ecarepro.data.network.model.NetworkStudentAttRepo
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.network.model.NetworkStudentListToMarkAtt
import com.app.ecarepro.data.network.model.NetworkStudentProfile
import com.app.ecarepro.data.network.model.NetworkSubAppreciationTypes
import com.app.ecarepro.data.network.model.NetworkSubInfractionTypes
import com.app.ecarepro.data.network.model.NetworkSubmitAssignReport
import com.app.ecarepro.data.network.model.NetworkTeacherAssignment
import com.app.ecarepro.data.network.model.NetworkTeachersTimetable
import com.app.ecarepro.data.network.model.NetworkThoughts
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.data.network.model.NetworkWhoLike
import com.app.ecarepro.data.network.model.PostAnswerPostData
import com.app.ecarepro.data.network.model.UploadPhotoRequest
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.data.network.model.UserLoginRequestDto
import com.app.ecarepro.data.network.model.UserProfileDto
import com.app.ecarepro.data.network.model.create_assignment.PostCreateAssignment
import com.app.ecarepro.data.network.model.post_leave_request.LeaveRequestData
import com.app.ecarepro.data.network.model.post_lesson.ActionOnLesson
import com.app.ecarepro.data.network.model.post_lesson.PostLesson
import com.app.ecarepro.data.network.model.post_mark_attedance.PostMarkAttedance
import com.app.ecarepro.data.network.model.post_question.AddQuestionPostData
import com.app.ecarepro.data.network.model.post_save_appreaction.PostSaveAppreciation
import com.app.ecarepro.data.network.model.post_save_infraction.PostSaveInfraction
import com.app.ecarepro.data.network.model.submit_assignment.PostSubmitAssignment
import com.app.ecarepro.model.ClassMateResponse
import com.app.ecarepro.model.StudentTeacherResponse
import com.app.ecarepro.ui.appuserreport.AppUserReportResponse
import com.app.ecarepro.ui.appuserreport.AppUserWebResponse
import com.app.ecarepro.ui.attendance_section.AttendanceResponse
import com.app.ecarepro.ui.award.ExcellenceAwardResponse
import com.app.ecarepro.ui.medicalcard.medical_class.StudentMedicalCardResponse
import com.app.ecarepro.ui.medicine_issue.MedicineIsuueModel
import com.app.ecarepro.ui.statical.StaticGraphResponse
import com.app.ecarepro.ui.studentId.StudentCardResponse
import com.app.ecarepro.ui.studentId.StudentIDRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.app.ecarepro.ui.survey.SurveyListResponse

import com.app.ecarepro.data.network.model.NetworkAcademicPerformance
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkAlbumType
import com.app.ecarepro.data.network.model.NetworkAppointments
import com.app.ecarepro.data.network.model.NetworkAppreciations
import com.app.ecarepro.data.network.model.NetworkAssignRollNo
import com.app.ecarepro.data.network.model.NetworkClassTeacher
import com.app.ecarepro.data.network.model.NetworkClassTeacherOf
import com.app.ecarepro.data.network.model.NetworkEBook
import com.app.ecarepro.data.network.model.NetworkFavorites
import com.app.ecarepro.data.network.model.NetworkGenerateTokenFeePay
import com.app.ecarepro.data.network.model.NetworkInfractions
import com.app.ecarepro.data.network.model.NetworkLeaveReport
import com.app.ecarepro.data.network.model.NetworkLibraryDTL
import com.app.ecarepro.data.network.model.NetworkMediaGallery
import com.app.ecarepro.data.network.model.NetworkOutPassReport
import com.app.ecarepro.data.network.model.NetworkPhotoAlbum
import com.app.ecarepro.data.network.model.NetworkProfileAttendanceDTL
import com.app.ecarepro.data.network.model.NetworkQuestionBank
import com.app.ecarepro.data.network.model.NetworkRechargeLog
import com.app.ecarepro.data.network.model.NetworkRouteList
import com.app.ecarepro.data.network.model.NetworkSMSBalnceInfo
import com.app.ecarepro.data.network.model.NetworkSMSConsumption
import com.app.ecarepro.data.network.model.NetworkSmsMsgReport
import com.app.ecarepro.data.network.model.NetworkStoppage
import com.app.ecarepro.data.network.model.NetworkStudentToMarkTransAttendane
import com.app.ecarepro.data.network.model.NetworkTeacherSyllabus
import com.app.ecarepro.data.network.model.NetworkTimeTableViewer
import com.app.ecarepro.data.network.model.NetworkTransAttendanceReport
import com.app.ecarepro.data.network.model.NetworkVideoAlbum
import com.app.ecarepro.data.network.model.NetworkVideoAlbumDTL
import com.app.ecarepro.data.network.model.PostLeaveAction
import com.app.ecarepro.data.network.model.create_syllabus.PostSyllabus
import com.app.ecarepro.data.network.model.postQuestionBank.NetworkPostQuestionBank
import com.app.ecarepro.data.network.model.post_roll_no.AssignRollNoBodyItem
import com.app.ecarepro.data.network.model.post_trans_att.PostStudentToMarkAtt
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankChapters
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankCreate
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankSubject
import com.app.ecarepro.model.FeeSummery
import com.app.ecarepro.ui.survey.SurveyQuestionsResponse
import com.app.ecarepro.ui.survey.SurveyQuestionsSubmitRequest
interface UserService {
    @GET("User/Verify")
    suspend fun verifyUser(
        @Query("SchCode") schoolCode: String,
        @Query("Username") username: String
    ): NetworkUserDetailsDto

    @POST("User/GetCredentials")
    suspend fun getCredentials(
        @Body request: GetCredentialsRequest,
    ): NetworkUserDetailsDto

    @POST("User/Login")
    suspend fun login(
        @Body request: UserLoginRequestDto,
    ): LoginResponseDto


    @GET("Academic/ClassSyllabus")
    suspend fun getClassSyllabus(): NetworkClassSyllabus

    @GET("Academic/ActivityCaledar")
    suspend fun getActivityCaledar(): NetworkActivityCalender


    @GET("Library/DTL")
    suspend fun getLibraryDTL(): NetworkLibraryDTL





    @GET("Academic/TeachersAssignment")
    suspend fun teachersAssignment(
        @Query("ID") iD: String,
    ): NetworkTeacherAssignment


    @GET("Library/BookDTL")
    suspend fun getBookDetails(
        @Query("BookID") bookID: Int,
        @Query("ID") id: Int,
    ): NetworkBookDetails

    @GET("Library/Search")
    suspend fun getLibrarySearch(
        @Query("query") query: String,
        @Query("pg") pg: Int,
    ): NetworkBookDetails

    @GET("Questionnaire/List")
    suspend fun getQuestionnaireList(
        @Query("pg") pg: Int,
        @Query("myque") myque: Boolean,
    ): NetworkQuestionnaire

    @GET("Staff/MyClass")
    suspend fun staffMyClass(
        /* @Query("SubID") subID: Int,
         @Query("ID") iD: Int*/
    ): NetworkMyClass

    @GET("Staff/MyClass")
    suspend fun staffMyClass(
         @Query("SubID") subID: Int,
         @Query("OnlyClass") onlyClass: Boolean
    ): NetworkMyClass

    @GET("Staff/Payslip")
    suspend fun getPayslip(): NetworkPaySlip

    @GET("Thoughts/List")
    suspend fun getThoughts(
        @Query("pg") pg: Int,
        @Query("dir") dir: Int,
        @Query("mythoughts") mythoughts: Boolean
    ): NetworkThoughts


    @GET("Thoughts/Like")
    suspend fun thoughtsLike(
        @Query("ThID") thID: Int,
        @Query("Like") like: Boolean
    ): CommonResponse

    @GET("Thoughts/WhoLiked")
    suspend fun whoLiked(
        @Query("ThID") thID: Int
    ): NetworkWhoLike

    @POST("Thoughts/Create")
    suspend fun thoughtsCreate(
        @Body request: AddThoughtsPostData,
    ): CommonResponse

    @GET("Thoughts/Delete")
    suspend fun thoughtsDelete(
        @Query("ThID") thID: Int
    ): CommonResponse


    @GET("Questionnaire/Like")
    suspend fun questionnaireLike(
        @Query("QID") qID: Int,
        @Query("Like") like: Boolean
    ): CommonResponse

    @GET("Questionnaire/DeleteAnswer")
    suspend fun deleteAnswer(
        @Query("AnsID") ansID: Int
    ): CommonResponse

    @GET("Questionnaire/AnswerList")
    suspend fun answerList(
        @Query("QID") qID: Int
    ): NetworkAnswerDetails

    @POST("Questionnaire/PostAnswer")
    suspend fun postAnswer(
        @Body request: PostAnswerPostData,
    ): CommonResponse

    @POST("Questionnaire/AddQuestion")
    suspend fun addQuestion(
        @Body request: AddQuestionPostData,
    ): CommonResponse

    @GET("Leave/Status")
    suspend fun leaveListStatus(): NetworkLeaveListStatus

    @GET("User/MedicineIssued")
    suspend fun medicineIssued(): MedicineIsuueModel

    @POST("Leave/Apply")
    suspend fun leaveApply(
        @Body request: LeaveRequestData,
    ): CommonResponse

    @GET("Leave/Report")
    suspend fun leaveReport(
        @Query("Status") status: Int,
        @Query("ord") ord: Int,
        @Query("ApplType") applType: Int,
        @Query("pg") pg: Int
    ): NetworkLeaveReport

    @POST("Leave/Action")
    suspend fun leaveAction(
        @Body request: PostLeaveAction,
    ): CommonResponse

    @GET("Leave/Setting")
    suspend fun leaveSetting(): NetworkLeaveSetting

    @GET("Leave/Delete")
    suspend fun leaveDelete(
        @Query("LvID") lvID: Int
    ): CommonResponse

    @GET("DisciplineLog/InfractionTypes")
    suspend fun infractionTypes(): NetworkInfractionTypes

    @GET("DisciplineLog/SubInfractionTypes")
    suspend fun subInfractionTypes(
        @Query("InfrTypeID") infrTypeID: Int
    ): NetworkSubInfractionTypes

    @GET("DisciplineLog/InfractionInstance")
    suspend fun infractionInstance(
        @Query("InfrTypeID") infrTypeID: Int,
        @Query("InfrSubTypeID") InfrSubTypeID: Int,
        @Query("InfrTypeID") InfrTypeID: Int
    ): NetworkInfractionInstance


    @GET("DisciplineLog/AddInfraction")
    suspend fun addInfraction(
        @Query("StID") stID: Int
    ): NetworkAddInfraction

    @GET("DisciplineLog/Infractions")
    suspend fun getInfractions(
        @Query("StID") stID: Int
    ): NetworkInfractions


    @POST("DisciplineLog/SaveInfraction")
    suspend fun saveInfraction(
        @Body request: PostSaveInfraction,
    ): CommonResponse

    @GET("Report/StudentList")
    suspend fun getStudentList(
        @Query("ScholarType") scholarType: Int,
        @Query("ShowAll") showAll: Boolean
    ): NetworkStudentList


    @GET("Report/StudentMedicalCard")
    suspend fun getStudentMedicalCard(
        @Query("StId") StId: String
    ): StudentMedicalCardResponse

    @POST("Student/UploadIDCardImg")
    suspend fun uploadPhoto(
        @Body request: StudentIDRequest
    ): CommonResponse

    @GET("Student/IDCard")
    suspend fun getStudentIDCard(
    ): StudentCardResponse

    @GET("DisciplineLog/AddAppreciation")
    suspend fun addAppreciation(
        @Query("StID") stID: Int
    ): NetworkAddAppreciation

    @GET("DisciplineLog/Appreciations")
    suspend fun getAppreciations(
        @Query("StID") stID: Int
    ): NetworkAppreciations

    @GET("DisciplineLog/SubAppreciationTypes")
    suspend fun subAppreciationTypes(
        @Query("AprID") aprID: Int
    ): NetworkSubAppreciationTypes

    @GET("DisciplineLog/AppreciationInstance")
    suspend fun appreciationInstance(
        @Query("AprSubID") aprSubID: Int,
        @Query("StID") stID: Int,
    ): NetworkAppreciationInstance


    @POST("DisciplineLog/SaveAppreciation")
    suspend fun saveAppreciation(
        @Body request: PostSaveAppreciation
    ): CommonResponse

    @GET("Academic/Assignment")
    suspend fun assignment(): NetworkAssignments

    @POST("Academic/SubmitAssignment")
    suspend fun submitAssignment(
        @Body request: PostSubmitAssignment,
    ): CommonResponse




    @GET("Academic/DeleteAssignment")
    suspend fun deleteAssignment(
        @Query("ID") iD: String,
    ): CommonResponse

    @GET("Staff/MySubjects")
    suspend fun mySubjects(
        @Query("ClassID") classID: Int
    ): NetworkMySubjects

    @GET("Staff/Subjects")
    suspend fun staffSubjects(
        @Query("ClassSTD") classSTD: Int
    ): NetworkMySubjects

    @POST("Academic/CreateAssignment")
    suspend fun createAssignment(
        @Body request: PostCreateAssignment,
    ): CommonResponse

    @GET("Academic/ViewAssignment")
    suspend fun viewAssignment(
        @Query("ID") iD: String,
    ): NetworkViewAssignment

    @GET("Academic/AssignmnetSubmissionRPT")
    suspend fun assignmnetSubmissionRPT(
        @Query("ID") iD: String,
        @Query("NotSubmitted") notSubmitted: Boolean,
    ): NetworkSubmitAssignReport

    @GET("Academic/OfflineSubmited")
    suspend fun offlineSubmited(
        @Query("ID") iD: String,
        @Query("StID") stID: Int,
        @Query("SubmissitedOn") submissitedOn: String,
    ): CommonResponse

    @GET("Staff/Attendance")
    suspend fun staffAttendance(
        @Query("Month") month: Int,
        @Query("Year") year: Int,
    ): NetworkStaffAttendence

    @GET("Report/Statistical")
    suspend fun statistical(): StaticGraphResponse
    @GET("Report/AppUsersCount")
    suspend fun appUsersCount(): AppUserReportResponse

    @GET("Report/AppUsersDTL")
    suspend fun appUsersWeb(@Query("UserType") userType:String): AppUserWebResponse
    @GET("Student/Attendance")
    suspend fun getAttendance(
        @Query("From") from: String,
        @Query("Till") till: String,
        @Query("YrID") yrID: String,
    ): AttendanceResponse

    @GET("Academic/TeachersTimetable")
    suspend fun teachersTimetable(
        @Query("ID") id: String
    ): NetworkTeachersTimetable

    @GET("Report/Birthday")
    suspend fun birthday(
        @Query("UserType") userType: Int,
        @Query("RptType") rptType: Int,
        @Query("MonthNo") monthNo: Int,
        @Query("Date") date: String,
    ): NetworkBirthday

    @GET("ReportCard/DTL")
    suspend fun reportCardDTL(
        @Query("StID") stID: Int
    ): NetworkReportCardDetails

    @GET("Staff/MarkAttendance")
    suspend fun markAttendance(): NetworkMarkAttendance

    @GET("Staff/StudentListToMarkAtt")
    suspend fun getStudentListToMarkAtt(
        @Query("ClassID") classID: Int,
        @Query("SubID") subID: Int,
        @Query("AttDate") attDate: String
    ): NetworkStudentListToMarkAtt

    @POST("Staff/PostAttendance")
    suspend fun postMarkAttendance(
        @Body request: PostMarkAttedance,
    ): CommonResponse

    @GET("Staff/LessonPlanList")
    suspend fun getLessonPlanList(
        @Query("pg") pg: Int,
        @Query("ID") id: String,
    ): NetworkLessonPlanList

    @GET("Staff/LessonPlanFilter")
    suspend fun getLessonPlanFilter(
        @Query("Filter") filter: String,
        @Query("From") from: String,
        @Query("Till") till: String,
        @Query("ClassIds") classIds: String,
        @Query("SubIds") subIds: String,
        @Query("Status") status: Int,


        ): NetworkLessonPlanList

    @GET("Staff/LessonPlanDTL")
    suspend fun getLessonPlanDTL(
        @Query("ID") id: String,
        @Query("TeacherID") teacherID: Int
    ): NetworkLessonPlanDTL

    @GET("Report/StaffList")
    suspend fun getStaffList(): NetworkStaffList

    @GET("Report/StaffProfile")
    suspend fun getStaffProfile(
        @Query("SID") sId: Int
    ): NetworkStaffProfile

    @POST("User/ChangeUsername")
    suspend fun changeUsername(
        @Body request: ChangeUserNameRequestDto,
    ): CommonResponse

    @POST("User/ChangePassword")
    suspend fun changePassword(
        @Body request: ChangeUserNameRequestDto,
    ): CommonResponse

    @POST("Staff/PostLessonPlan")
    suspend fun postLessonPlan(
        @Body request: PostLesson,
    ): CommonResponse

    @GET("Staff/CreateLessonPlan")
    suspend fun createLessonPlan(): NetworkCreateLesson

    @GET("Report/StudentProfile")
    suspend fun getStudentProfile(
        @Query("StID") sId: Int
    ): NetworkStudentProfile

    @GET("Report/AttendanceYrID")
    suspend fun getSAttendanceYrID(
        @Query("StID") sId: Int,
        @Query("YrID") yrID: Int
    ): NetworkProfileAttendanceDTL

    @GET("Report/FeeSummaryYrID")
    suspend fun getFeeSummaryYrID(
        @Query("StID") sId: Int,
        @Query("YrID") yrID: Int
    ): FeeSummery

    @GET("Student/AcademicPerformance")
    suspend fun getAcademicPerformance(
        @Query("StID") sId: Int,
        @Query("YrID") yrID: Int
    ): NetworkAcademicPerformance

    @GET("Report/AttendanceSummary")
    suspend fun getAttendanceSummary(
        @Query("AttDate") attDate: String
    ): NetworkAttedanceSummary

    @GET("Report/ClassAttendance")
    suspend fun getClassAttendance(
        @Query("ID") id: String,
        @Query("AttDate") attDate: String
    ): NetworkClassAttendance


    @GET("Student/Attendance")
    suspend fun getStudentAttendance(
        @Query("From") from: String,
        @Query("Till") till: String,
        @Query("YrID") yrID: String,
        @Query("ID") iD: String,
    ): NetworkStudentAttRepo


    @POST("Staff/LessonPlanAction")
    suspend fun lessonPlanAction(
        @Body request: ActionOnLesson,
    ): CommonResponse

    @GET("User/UsernameAvailability")
    suspend fun checkUsernameAvailability(
        @Query("NewUsername") newUsername: String
    ): CommonResponse

    @GET("User/MyProfile")
    suspend fun getUserProfile(
        @Query("Edit") edit: Boolean = true
    ): UserProfileDto

    @POST("User/UploadProfileIMG")
    suspend fun uploadProfileIMG(
        @Body request: UploadPhotoRequest
    ): CommonResponse


    @GET("Academic/ExcellenceAward")
    suspend fun excellenceAward(
    ): ExcellenceAwardResponse

    @GET("User/AppDashboard")
    suspend fun getUserDashboard(
        @Query("Device") device: Int = 1
    ): UserDashboardDto


    @GET("Report/Classteacher")
    suspend fun getClassTeacher(
    ): NetworkClassTeacher

    @GET("Academic/TimetableViewer")
    suspend fun getTimetableViewer(
    ): NetworkTimeTableViewer

    @GET("Academic/ClassAssignment")
    suspend fun getClassAssignment(
        @Query("ID") id: String
    ): NetworkAssignments

    @GET("Academic/ClassTimetable")
    suspend fun classTimetable(
        @Query("ID") id: String?
    ): NetworkTeachersTimetable

    @GET("Appointment/Overview")
    suspend fun appointmentOverview(
        @Query("AppDate") appDate: String,
        @Query("TillDate") tillDate: String,
        @Query("all") all: Boolean,
    ): NetworkAppointments

    @GET("Appointment/Action")
    suspend fun appointmentAction(
        @Query("Act") act: Int,
        @Query("AppId") appId: Int
    ): CommonResponse

    @GET("Transport/Routes")
    suspend fun routesList(): NetworkRouteList

    @GET("Transport/Stoppage")
    suspend fun stoppageList(
        @Query("RouteIDs") routeIDs: String,
        @Query("Trip") trip: Int
    ): NetworkStoppage

    @GET("Transport/StudentToMarkTransAttendane")
    suspend fun studentToMarkTransAttendane(
        @Query("RouteID") routeIDs: String,
        @Query("StopID") stopID: Int,
        @Query("Trip") trip: Int,
        @Query("AttDate") attDate: String,
        @Query("StopIDs") stopIDs: String
    ): NetworkStudentToMarkTransAttendane

    @GET("Transport/StudentToDrop")
    suspend fun studentToDrop(
        @Query("RouteID") routeID : Int,
        @Query("StopID") stopID: Int,
         @Query("AttDate") attDate: String,
     ): NetworkStudentToMarkTransAttendane

    @POST("Transport/PostTransAttendance")
    suspend fun postTransAttendance(
        @Body request: PostStudentToMarkAtt,
    ): CommonResponse

    @GET("Transport/TransAttendanceReport")
    suspend fun transAttendanceReport(
        @Query("RouteID") routeID : Int,
        @Query("StopIds") stopID: String,
        @Query("AttDate") attDate: String,
    ): NetworkTransAttendanceReport

    @GET("Transport/OutPassReport")
    suspend fun getOutPassReport(
        @Query("AttDate") attDate: String,
    ): NetworkOutPassReport

    @GET("Admin/StudentListToAssignRollNo")
    suspend fun getStudentListToAssignRollNo(
        @Query("ID") iD: String,
        @Query("Orderby") orderby: Int
    ): NetworkAssignRollNo

    @GET("Staff/ClassTeacherOf")
    suspend fun getClassTeacherOf(  ): NetworkClassTeacherOf

    @POST("Admin/AssignRollNumber")
    suspend fun assignRollNumber(
        @Body request: List<AssignRollNoBodyItem>
    ): CommonResponse

    @GET("Transport/DropToStudent")
    suspend fun dropToStudent(
        @Query("StID") stID : Int,
        @Query("AttDate") attDate: String,
        @Query("hasDroped") hasDropped: Boolean,
    ): NetworkStudentToMarkTransAttendane

    @GET("Report/AppMsgUses")
    suspend fun getAppMsgUses(
        @Query("FromDate") fromDate : String,
        @Query("ToDate") toDate: String,
        @Query("ID") iD: String,
    ): NetworkSmsMsgReport

    @GET("Report/SMSUses")
    suspend fun getSMSUses(
        @Query("FromDate") fromDate : String,
        @Query("ToDate") toDate: String,
        @Query("ID") iD: String,
    ): NetworkSmsMsgReport



    @GET("School/SMSConsumption")
    suspend fun getSMSConsumption(
        @Query("FromDate") fromDate : String,
        @Query("ToDate") toDate: String
    ): NetworkSMSConsumption

    @GET("School/SMSBalnceInfo")
    suspend fun getSMSBalnceInfo(  ): NetworkSMSBalnceInfo

    @GET("School/RechargeLog")
    suspend fun getRechargeLog(
        @Query("FromDate") fromDate : String,
        @Query("ToDate") toDate: String
    ): NetworkRechargeLog


    @GET("User/GenerateToken")
    suspend fun getGenerateToken(
        @Query("Device") device : Int
    ): NetworkGenerateTokenFeePay

    @GET("Student/Teachers")
    suspend fun getStudentTeachers(
    ): StudentTeacherResponse

    @GET("Student/Classmates")
    suspend fun getClassmates(
    ): ClassMateResponse

    @GET("Admin/StudentListToAssignHouse")
    suspend fun getStudentListToAssignHouse(
        @Query("ID") id: String,
        @Query("Orderby") orderBy: String
    ): UserDashboardDto

    @POST("Admin/AssignHouse")
    suspend fun assignHouse(
        @Body request: AssignHouseRequest
    ): CommonResponse

    @GET("User/UserUndertaking")
    suspend fun getUserUndertaking(): String

    @POST("User/SaveUndertakingAckowledgement")
    suspend fun saveUserUndertaking(
        @Query("UtID") id:String,
    ): CommonResponse

    @GET("Academic/QuestionPaper")
    suspend fun getQuestionPaper(
        @Query("ClassID") classID: Int,
        @Query("YrID") yrID: Int
    ): NetworkQuestionPaper
    @GET("Survey/List")
    suspend fun surveyList(
        @Query("pg") pg: Int=1,
        @Query("isReport") isReport: Boolean=false,
    ): SurveyListResponse

    @GET("Survey/Questions")
    suspend fun surveyQuestions(
        @Query("ID") id: String
    ): SurveyQuestionsResponse

    @POST("Survey/PostAnswer")
    suspend fun submitSurveyQuestions(
        @Body model: SurveyQuestionsSubmitRequest
    ): CommonResponse

    @GET("Gallery/PhotoAlbumTypes")
    suspend fun getPhotoAlbumTypes( ): NetworkAlbumType

    @GET("Gallery/PhotoAlbums")
    suspend fun getPhotoAlbums(
        @Query("typeID") typeID: Int,
        @Query("pg") pg: Int,
    ): NetworkPhotoAlbum

    @GET("Gallery/PhotoAlbumDTL")
    suspend fun getPhotoAlbumDTL(
        @Query("ID") iD: String,
        @Query("pg") pg: Int,
    ): NetworkAlbumPhotoDetails

    @GET("Gallery/VideoAlbums")
    suspend fun getVideoAlbums(
        @Query("pg") pg: Int,
    ): NetworkVideoAlbum

    @GET("Gallery/VideoAlbumDTL")
    suspend fun getVideoAlbumDTL(
        @Query("ID") id: String,
        @Query("pg") pg: Int,
    ): NetworkVideoAlbumDTL

    @GET("Gallery/Favorites")
    suspend fun getFavorites(
        @Query("pg") pg: Int,
    ): NetworkFavorites

    @GET("Gallery/ManageFavorites")
    suspend fun manageFavorites(
        @Query("ID") id: String,
        @Query("GalleryType") galleryType: Int,
        @Query("Action") action: String
    ): CommonResponse

    @GET("Gallery/Like")
    suspend fun manageLikes(
        @Query("ID") id: String,
        @Query("GalleryType") galleryType: Int,
        @Query("like") like: Boolean
    ): CommonResponse

    @GET("Gallery/MediaGallery")
    suspend fun getMediaGallery(
        @Query("pg") pg: Int,
        @Query("QueryType") queryType: Int,
        @Query("Year") year: Int,
        @Query("Date") date: String,
        @Query("Query") query: String
    ): NetworkMediaGallery

    @GET("QuestionBank/MyQuestionBank")
    suspend fun getMyQuestionBank(  ): NetworkQuestionBank

    @GET("QuestionBank/Create")
    suspend fun getQuestionBankCreate(  ): NetworkQuestionBankCreate

    @GET("QuestionBank/GetSubject")
    suspend fun getQuestionBankSubject(
        @Query("ClassID") classID: Int
    ): NetworkQuestionBankSubject

    @GET("QuestionBank/GetChapters")
    suspend fun getQuestionBankChapters(
        @Query("ClassID") classID: Int,
        @Query("SubID") subID: Int
    ): NetworkQuestionBankChapters

    @POST("QuestionBank/PostQuestion")
    suspend fun submitPostQuestion(
        @Body model: NetworkPostQuestionBank
    ): CommonResponse


    @GET("QuestionBank/DeleteQuestion")
    suspend fun getDeleteQuestion(
        @Query("ID") id: String
    ): CommonResponse

    @GET("Library/eBooks")
    suspend fun getEBook(
        @Query("query") query: String ,
        @Query("mode") mode: Int
    ): NetworkEBook

    @GET("Library/OnlineCode")
    suspend fun getEBookDetails(
        @Query("AccessionNo") accessionNo: String
    ): CommonResponse

    @GET("Admin/Syllabuses")
    suspend fun getTeacherSyllabuses( ): NetworkTeacherSyllabus

    @POST("Admin/SaveSyllabus")
    suspend fun saveSyllabus(
        @Body request: PostSyllabus,
    ): CommonResponse
    @GET("Admin/DeleteSyllabus")
    suspend fun deleteSyllabus(
        @Query("ID") ID: String
    ): CommonResponse









}