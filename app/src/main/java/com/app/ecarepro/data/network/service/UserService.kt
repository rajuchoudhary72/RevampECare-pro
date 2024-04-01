package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.GetCredentialsRequest
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkClassSyllabus
import com.app.ecarepro.data.network.model.NetworkLatestBook
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkPaySlip
import com.app.ecarepro.data.network.model.NetworkQuestionnaire
import com.app.ecarepro.data.network.model.NetworkThoughts
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.NetworkWhoLike
import com.app.ecarepro.data.network.model.UserLoginRequestDto
import com.app.ecarepro.data.network.model.AddThoughtsPostData
import com.app.ecarepro.data.network.model.NetworkAddAppreciation
import com.app.ecarepro.data.network.model.NetworkAddInfraction
import com.app.ecarepro.data.network.model.NetworkAnswerDetails
import com.app.ecarepro.data.network.model.NetworkAppreciationInstance
import com.app.ecarepro.data.network.model.NetworkAssignments
import com.app.ecarepro.data.network.model.NetworkBirthday
import com.app.ecarepro.data.network.model.NetworkInfractionInstance
import com.app.ecarepro.data.network.model.NetworkInfractionTypes
import com.app.ecarepro.data.network.model.NetworkLeaveListStatus
import com.app.ecarepro.data.network.model.NetworkLeaveSetting
import com.app.ecarepro.data.network.model.NetworkMySubjects
import com.app.ecarepro.data.network.model.NetworkStaffAttendence
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.network.model.NetworkSubAppreciationTypes
import com.app.ecarepro.data.network.model.NetworkSubInfractionTypes
import com.app.ecarepro.data.network.model.NetworkSubmitAssignReport
import com.app.ecarepro.data.network.model.NetworkTeacherAssignment
import com.app.ecarepro.data.network.model.NetworkTeachersTimetable
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.data.network.model.PostAnswerPostData
import com.app.ecarepro.data.network.model.create_assignment.PostCreateAssignment
import com.app.ecarepro.data.network.model.post_leave_request.LeaveRequestData
import com.app.ecarepro.data.network.model.post_question.AddQuestionPostData
import com.app.ecarepro.data.network.model.post_save_appreaction.PostSaveAppreciation
import com.app.ecarepro.data.network.model.post_save_infraction.PostSaveInfraction
import com.app.ecarepro.data.network.model.submit_assignment.PostSubmitAssignment
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.app.ecarepro.data.network.model.ChangeUserNameRequestDto

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
    suspend fun getClassSyllabus(  ): NetworkClassSyllabus

     @GET("Academic/ActivityCaledar")
    suspend fun getActivityCaledar(  ): NetworkActivityCalender


     @GET("Library/DTL")
    suspend fun getLibraryDetails(  ): NetworkLatestBook

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

     @GET("Staff/Payslip")
    suspend fun getPayslip( ): NetworkPaySlip

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
    suspend fun leaveListStatus( ): NetworkLeaveListStatus

    @POST("Leave/Apply")
    suspend fun leaveApply(
        @Body request: LeaveRequestData,
    ): CommonResponse

    @GET("Leave/Setting")
    suspend fun leaveSetting( ): NetworkLeaveSetting

    @GET("Leave/Delete")
    suspend fun leaveDelete(
        @Query("LvID") lvID: Int
    ): CommonResponse

    @GET("DisciplineLog/InfractionTypes")
    suspend fun infractionTypes(  ): NetworkInfractionTypes

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


    @POST("DisciplineLog/SaveInfraction")
    suspend fun saveInfraction(
        @Body request: PostSaveInfraction,
    ): CommonResponse

    @GET("Report/StudentList")
    suspend fun getStudentList(
        @Query("ScholarType") scholarType: Int,
        @Query("ShowAll") showAll: Boolean
    ): NetworkStudentList

    @GET("DisciplineLog/AddAppreciation")
    suspend fun addAppreciation(
        @Query("StID") stID: Int
    ): NetworkAddAppreciation

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
    suspend fun assignment( ): NetworkAssignments

    @POST("Academic/SubmitAssignment")
    suspend fun submitAssignment(
        @Body request: PostSubmitAssignment,
    ): CommonResponse


    @GET("Academic/TeachersAssignment")
    suspend fun teachersAssignment( ): NetworkTeacherAssignment

    @GET("Academic/DeleteAssignment")
    suspend fun deleteAssignment(
        @Query("ID") iD: String,
    ): CommonResponse

    @GET("Staff/MySubjects")
    suspend fun mySubjects( ): NetworkMySubjects

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

    @POST("User/ChangeUsername")
    suspend fun changeUsername(
        @Body request: ChangeUserNameRequestDto,
    ): CommonResponse

    @POST("User/ChangePassword")
    suspend fun changePassword(
        @Body request: ChangeUserNameRequestDto,
    ): CommonResponse

    @GET("User/UsernameAvailability")
    suspend fun checkUsernameAvailability(
        @Query("NewUsername") newUsername: String
    ): CommonResponse


}