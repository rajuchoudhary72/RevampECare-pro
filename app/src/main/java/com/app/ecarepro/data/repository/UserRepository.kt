package com.app.ecarepro.data.repository

import com.app.ecarepro.AssignHouseRequest
import com.app.ecarepro.data.network.model.Attachment
import com.app.ecarepro.data.network.model.ChangeUserNameRequestDto
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkAcademicPerformance
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkAddAppreciation
import com.app.ecarepro.data.network.model.NetworkAddInfraction
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkAlbumType
import com.app.ecarepro.data.network.model.NetworkAnswerDetails
import com.app.ecarepro.data.network.model.NetworkAppointments
import com.app.ecarepro.ui.survey.SurveyListResponse
import com.app.ecarepro.model.Student
import com.app.ecarepro.data.network.model.Form

import com.app.ecarepro.data.network.model.NetworkAppreciationInstance
import com.app.ecarepro.data.network.model.NetworkAppreciations
import com.app.ecarepro.data.network.model.NetworkAssignRollNo
import com.app.ecarepro.data.network.model.NetworkAssignments
import com.app.ecarepro.data.network.model.NetworkAttedanceSummary
import com.app.ecarepro.data.network.model.NetworkBirthday
import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkBusLocation
import com.app.ecarepro.data.network.model.NetworkClassAttendance
import com.app.ecarepro.data.network.model.NetworkClassSyllabus
import com.app.ecarepro.data.network.model.NetworkClassTeacher
import com.app.ecarepro.data.network.model.NetworkClassTeacherOf
import com.app.ecarepro.data.network.model.NetworkCreateLesson
import com.app.ecarepro.data.network.model.NetworkEBook
import com.app.ecarepro.data.network.model.NetworkEditProfile
import com.app.ecarepro.data.network.model.NetworkFavorites
import com.app.ecarepro.data.network.model.NetworkGenerateTokenFeePay
import com.app.ecarepro.data.network.model.NetworkInfractionInstance
import com.app.ecarepro.data.network.model.NetworkInfractionTypes
import com.app.ecarepro.data.network.model.NetworkInfractions
import com.app.ecarepro.data.network.model.Department
import com.app.ecarepro.data.network.model.Designation
import com.app.ecarepro.data.network.model.Employee
import com.app.ecarepro.data.network.model.Purpose

import com.app.ecarepro.data.network.model.NetworkLeaveListStatus
import com.app.ecarepro.data.network.model.NetworkLeaveReport
import com.app.ecarepro.data.network.model.NetworkLeaveSetting
import com.app.ecarepro.data.network.model.NetworkLessonPlanDTL
import com.app.ecarepro.data.network.model.NetworkLessonPlanList
import com.app.ecarepro.data.network.model.NetworkLibraryDTL
import com.app.ecarepro.data.network.model.NetworkMarkAttendance
import com.app.ecarepro.data.network.model.NetworkMediaGallery
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkMySubjects
import com.app.ecarepro.data.network.model.NetworkOutPassReport
import com.app.ecarepro.data.network.model.NetworkPaySlip
import com.app.ecarepro.data.network.model.NetworkPhotoAlbum
import com.app.ecarepro.data.network.model.NetworkProfileAttendanceDTL
import com.app.ecarepro.data.network.model.NetworkQuestionBank
import com.app.ecarepro.data.network.model.NetworkQuestionPaper
import com.app.ecarepro.data.network.model.NetworkQuestionnaire
import com.app.ecarepro.data.network.model.NetworkRechargeLog
import com.app.ecarepro.data.network.model.NetworkReportCardDetails
import com.app.ecarepro.data.network.model.NetworkRouteList
import com.app.ecarepro.data.network.model.NetworkSMSBalnceInfo
import com.app.ecarepro.data.network.model.NetworkSMSConsumption
import com.app.ecarepro.data.network.model.NetworkSmsMsgReport
import com.app.ecarepro.data.network.model.NetworkStaffAttendence
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.data.network.model.NetworkStaffProfile
import com.app.ecarepro.data.network.model.NetworkStoppage
import com.app.ecarepro.data.network.model.NetworkStudentAttRepo
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.network.model.NetworkStudentListToMarkAtt
import com.app.ecarepro.data.network.model.NetworkStudentProfile
import com.app.ecarepro.data.network.model.NetworkStudentToMarkTransAttendane
import com.app.ecarepro.data.network.model.NetworkSubAppreciationTypes
import com.app.ecarepro.data.network.model.NetworkSubInfractionTypes
import com.app.ecarepro.data.network.model.NetworkSubmitAssignReport
import com.app.ecarepro.data.network.model.NetworkTeacherAssignment
import com.app.ecarepro.data.network.model.NetworkTeacherSyllabus
import com.app.ecarepro.data.network.model.NetworkTeachersTimetable
import com.app.ecarepro.data.network.model.NetworkThoughts
import com.app.ecarepro.data.network.model.NetworkTimeTableViewer
import com.app.ecarepro.data.network.model.NetworkTransAttendanceReport
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.NetworkVehicleNumber
import com.app.ecarepro.data.network.model.NetworkVideoAlbum
import com.app.ecarepro.data.network.model.NetworkVideoAlbumDTL
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.data.network.model.NetworkWhoLike
import com.app.ecarepro.data.network.model.Profile
import com.app.ecarepro.data.network.model.SendMessageRequest
import com.app.ecarepro.data.network.model.StaffAttendanceDetails
import com.app.ecarepro.data.network.model.UploadPhotoRequest
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.data.network.model.UserUndertakingModule
import com.app.ecarepro.data.network.model.VisitorDetails
import com.app.ecarepro.data.network.model.create_syllabus.PostSyllabus
import com.app.ecarepro.data.network.model.postQuestionBank.NetworkPostQuestionBank
import com.app.ecarepro.data.network.model.post_leave_request.FileAttachment
import com.app.ecarepro.data.network.model.post_leave_request.HalfdayDTL
import com.app.ecarepro.data.network.model.post_mark_attedance.StudentAtt
import com.app.ecarepro.data.network.model.post_roll_no.AssignRollNoBodyItem
import com.app.ecarepro.data.network.model.post_trans_att.StuAtt
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankChapters
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankCreate
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankSubject
import com.app.ecarepro.model.ClassID_StID
import com.app.ecarepro.model.ClassMateResponse
import com.app.ecarepro.model.FeeSummery
import com.app.ecarepro.model.Staff
import com.app.ecarepro.model.StudentTeacherResponse
 import com.app.ecarepro.ui.appuserreport.AppUserReportResponse
import com.app.ecarepro.ui.appuserreport.AppUserWebResponse
import com.app.ecarepro.ui.attendance_section.AttendanceResponse
import com.app.ecarepro.ui.award.ExcellenceAwardResponse
import com.app.ecarepro.ui.edit_profile.model.update_profile.UpdateProfileModel
import com.app.ecarepro.ui.medicalcard.medical_class.StudentMedicalCardResponse
import com.app.ecarepro.ui.medicine_issue.MedicineIsuueModel
import com.app.ecarepro.ui.statical.StaticGraphResponse
import com.app.ecarepro.ui.studentId.StudentCardResponse
import com.app.ecarepro.ui.studentId.StudentIDRequest
import kotlinx.coroutines.flow.Flow
import com.app.ecarepro.ui.survey.SurveyQuestionsResponse
import com.app.ecarepro.ui.survey.SurveyQuestionsSubmitRequest
import retrofit2.http.Query

interface UserRepository {

    suspend fun verifyUser(schoolCode: String, username: String): NetworkUserDetailsDto
    suspend fun logout(): Flow<Result<Boolean>>

    suspend fun getCredentials(
        schoolCode: String,
        userType: Int,
        rcvOn: String,
        mobile: String?,
        email: String?
    ): NetworkUserDetailsDto

    suspend fun login(
        schoolCode: String,
        userName: String,
        password: String
    ): LoginResponseDto

    suspend fun changeUserName(
        changeUserNameRequestDto: ChangeUserNameRequestDto
    ): Flow<Result<CommonResponse>>
    suspend fun getStudents(): Flow<Result<List<Student>>>
    suspend fun getStaffs(): Flow<Result<List<Staff>>>

    suspend fun changePassword(
        password: String,
        confirmPassword: String
    ): Flow<Result<CommonResponse>>
    suspend fun getClassSyllabus(): NetworkClassSyllabus
    suspend fun getActivityCalender(): NetworkActivityCalender

    suspend fun getLibraryDTL(): NetworkLibraryDTL
     suspend fun getBookDetails(bookID: Int, id: Int): NetworkBookDetails
    suspend fun getLibrarySearch(query: String, pg: Int): NetworkBookDetails
    fun getFormData(): Flow<Result<List<Form>>>
    fun getFormDataPurpose(): Flow<Result<List<Purpose>>>

    fun getFormDataDepartment(): Flow<Result<List<Department>>>

    fun getFormDataDesignationWithDepartment(departmentId:String): Flow<Result<List<Designation>>>
    fun getVisitorDetails(): Flow<Result<VisitorDetails>>

    fun getFormDataEmployee(departmentId:String, designation:String): Flow<Result<List<Employee>>>
    fun submitForm(formData:Map<String,String>): Flow<Result<String>>

    suspend fun staffMyClass(subID: Int, iD: Int): NetworkMyClass

    suspend fun staffMyClass(
         subID: Int,
         onlyClass: Boolean
    ): NetworkMyClass
    suspend fun getPayslip(): NetworkPaySlip

    suspend fun getThoughts(
        pg: Int,
        dir: Int,
        mythoughts: Boolean
    ): NetworkThoughts

    suspend fun thoughtsLike(
        thID: Int,
        like: Boolean
    ): CommonResponse

    suspend fun thoughtsDelete(thID: Int): CommonResponse

    suspend fun whoLiked(thID: Int): NetworkWhoLike

    suspend fun thoughtsCreate(quotation: String, author: String): CommonResponse

    suspend fun getQuestionnaireList(pg: Int, myque: Boolean): NetworkQuestionnaire

    suspend fun questionnaireLike(
        qID: Int,
        like: Boolean
    ): CommonResponse

    suspend fun answerList(qID: Int): NetworkAnswerDetails

    suspend fun postAnswer(qid: String, answer: String): CommonResponse

    suspend fun deleteAnswer(ansID: Int): CommonResponse
    suspend fun deleteQID(  QID: Int ): CommonResponse
    suspend fun addQuestion(
        question: String,
        attachment: String,
        fileURL: String,
        fileExt: String
    ): CommonResponse

    fun getUserProfile(): Flow<Result<Profile>>
    suspend fun getUserProfileEdit(
        edit: Boolean
    ): NetworkEditProfile

    suspend fun updateParentProfile(
         request: UpdateProfileModel
    ): CommonResponse

    fun uploadProfileIMG(uploadPhotoRequest: UploadPhotoRequest): Flow<Result<String>>

    suspend fun leaveListStatus(): NetworkLeaveListStatus
    suspend fun medicineIsuueModel(): MedicineIsuueModel

    suspend fun leaveReport(
        status: Int,
        ord: Int,
        applType: Int,
        pg: Int
    ): NetworkLeaveReport


    suspend fun leaveAction(
        applType: Int,
        lvID: Int,
        action: Int,
        forwardedTo: Int,
    ): CommonResponse

    suspend fun leaveApply(
        leaveID: Int,
        fromDate: String,
        tillDate: String,
        duration: Double,
        halfdayDTL: List<HalfdayDTL>?,
        reason: String,
        fileAttachment: FileAttachment?,

        ): CommonResponse

    suspend fun leaveSetting(): NetworkLeaveSetting

    suspend fun leaveDelete(lvID: Int): CommonResponse

    suspend fun getInfractionTypes(): NetworkInfractionTypes

    suspend fun getSubInfractionTypes(infrTypeID: Int): NetworkSubInfractionTypes

    suspend fun infractionInstance(
        infrTypeID: Int,
        InfrSubTypeID: Int,
        StID: Int
    ): NetworkInfractionInstance

    suspend fun addInfraction( stID: Int  ): NetworkAddInfraction

    suspend fun getAppreciations(
        stID: Int
    ): NetworkAppreciations

    suspend fun getInfractions(
        stID: Int
    ): NetworkInfractions

    suspend fun disciplineLogDeleteLog(
         id: String,
          type: Int
    ): CommonResponse


    suspend fun saveInfraction(
        action:Int,
        stID:Int,
        infrSubTypeID:Int,
        consID:Int,
        instance:Int,
        infractionOn:String,
        correctiveAction:String,

    ): CommonResponse


    suspend fun getStudentList(
       scholarType: Int,
         showAll: Boolean
    ): NetworkStudentList

    suspend fun getStudentMedicalCard(
       stID: String
    ): StudentMedicalCardResponse

    suspend fun getStudentTeachers(
    ): StudentTeacherResponse

    suspend fun getClassmates(
    ): ClassMateResponse
 suspend fun uploadPhoto(
       request: StudentIDRequest
    ): CommonResponse


    suspend fun getStudentIDCard(): StudentCardResponse

    suspend fun addAppreciation( stID: Int  ): NetworkAddAppreciation

    suspend fun subAppreciationTypes(
          aprID: Int
    ): NetworkSubAppreciationTypes

    suspend fun appreciationInstance(
         aprSubID: Int,
        stID: Int,
    ): NetworkAppreciationInstance

    suspend fun saveAppreciation(
        action:Int,
        stID:Int,
        aprSubID:Int,
        rwdID:Int,
        instance:Int,
        appreciationOn:String,
        remark:String,

        ): CommonResponse

    suspend fun assignment( ): NetworkAssignments

    suspend fun submitAssignment(
        id: String,
        asgID: Int,
        data: String,
        fileName: String,
        attachment: String,
        fileURL: String,
        fileExt: String
    ): CommonResponse

    suspend fun teachersAssignment(
        iD: String,
    ): NetworkTeacherAssignment

    suspend fun deleteAssignment(iD: String): CommonResponse

    suspend fun mySubjects(classID: Int): NetworkMySubjects

    suspend fun staffSubjects( classSTD: Int ): NetworkMySubjects

    suspend fun createAssignment(
        asgDate: String,
        asgID: Int,

        classID: Int,
        classIDs: String,
        data: String,
        file: String,
        id: String,
        isActive: Boolean,
        isFileRemoved: Boolean,
        multipleSubmission: Boolean,

        subjectID: Int,
        submitDate: String,
        title: String,
        lateSubmission: Boolean,
        attachments: List<com.app.ecarepro.data.network.model.Attachment>?,
        classID_StID: List<ClassID_StID>,
        stIDs: String?
    ): CommonResponse

    suspend fun viewAssignment(
          iD: String,
    ): NetworkViewAssignment

    suspend fun assignmentDTL(
          iD: String,
    ): NetworkViewAssignment

    suspend fun assignmnetSubmissionRPT(
          iD: String,
          notSubmitted: Boolean,
    ): NetworkSubmitAssignReport

    suspend fun offlineSubmited(
         iD: String,
          stID: Int,
         submissitedOn: String,
    ): CommonResponse

    suspend fun staffAttendance(
          month: Int,
          year: Int,
    ): NetworkStaffAttendence

    suspend fun statistical(
    ): StaticGraphResponse

    suspend fun appUserReportResponse(
    ): AppUserReportResponse

    suspend fun appUserReportWevResponse(
        userType:String
    ): AppUserWebResponse

    suspend fun getStaffAttendance(
        staffType: String? = null,
        date: String,
    ): Flow<Result<List<StaffAttendanceDetails>>>
    suspend fun getAttendance(
        from: String,
        till: String,
         yrID: String,
          ID: String,
    ): AttendanceResponse
    suspend fun teachersTimetable(
         id: String
    ): NetworkTeachersTimetable

    suspend fun classTimetable(
        id: String?
    ): NetworkTeachersTimetable

    suspend fun birthday(
          userType: Int,
         rptType: Int,
         monthNo: Int,
          date: String,
    ): NetworkBirthday
    suspend fun excellenceAward (): ExcellenceAwardResponse

    fun getUserDashboard(): Flow<Result<UserDashboardDto>>
    fun getStudentListToAssignHouse(id:String, orderBy:String): Flow<Result<UserDashboardDto>>
    fun assignHouse(request: AssignHouseRequest): Flow<Result<CommonResponse>>
    fun getUserUndertaking(): Flow<Result<String>>
    fun saveUserUndertaking(request: UserUndertakingModule): Flow<Result<String>>

    suspend fun reportCardDTL(
        stID: Int
    ): NetworkReportCardDetails

    suspend fun markAttendance(): NetworkMarkAttendance

    suspend fun getStudentListToMarkAtt(
        classID: Int,
        subID: Int,
        attDate: String
    ): NetworkStudentListToMarkAtt

    suspend fun postMarkAttedance(
        classID: Int,
        subID: Int,
        mode: Int,
        attDate: String,
        stuList: List<StudentAtt>
    ): CommonResponse

    suspend fun getLessonPlanList(page: Int, id: String): NetworkLessonPlanList

    suspend fun getLessonPlanFilter(
        filter: String,
        from: String,
        till: String,
        classIds: String,
        subIds: String,
        status: Int,


        ): NetworkLessonPlanList

    suspend fun getLessonPlanDTL(
        id: String,
        teacherID: Int
    ): NetworkLessonPlanDTL

    suspend fun getStaffList(): NetworkStaffList

    suspend fun getStaffProfile(sId: Int): NetworkStaffProfile

    suspend fun postLessonPlan(
        attachment: String,
        fileExt: String,
        fileURL: String,
        auditory: String,
        classIds: String,
        closure: String,
        extensionTopic: String,
        fileName: String,
        fromDate: String,
        introduction: String,
        kinestheticActivity: String,
        lPlnID: Int,
        learningOutcomes: String,
        objective: String,
        otherResources: String,
        resources: String,
        showToStudent: Boolean,
        subID: Int,
        tillDate: String,
        topic: String,
        youtubeLinks: String
    ): CommonResponse

    suspend fun createLessonPlan(): NetworkCreateLesson

    suspend fun getStudentProfile(sId: Int): NetworkStudentProfile

    suspend fun getSAttendanceYrID(
        sId: Int,
        yrID: Int
    ): NetworkProfileAttendanceDTL

    suspend fun getFeeSummaryYrID(
        sId: Int,
        yrID: Int
    ): FeeSummery

    suspend fun getAcademicPerformance(
        sId: Int,
        yrID: Int
    ): NetworkAcademicPerformance

    suspend fun getAttendanceSummary(
        attDate: String
    ): NetworkAttedanceSummary

    suspend fun getClassAttendance(
        id: String,
        attDate: String
    ): NetworkClassAttendance

    suspend fun getStudentAttendance(
        from: String,
        till: String,
        yrID: String,
        iD: String,
    ): NetworkStudentAttRepo

    suspend fun lessonPlanAction(
        lPlnID: Int,
        action: Int,
        rejectionComments: String,
    ): CommonResponse

    suspend fun getClassTeacher(
    ): NetworkClassTeacher

    suspend fun getTimetableViewer(
    ): NetworkTimeTableViewer

    suspend fun getClassAssignment(
        id: String
    ): NetworkAssignments


    suspend fun appointmentOverview(
        appDate: String,
        tillDate: String,
        all: Boolean,
    ): NetworkAppointments

    suspend fun appointmentAction(
        act: Int,
        appId: Int
    ): CommonResponse

    suspend fun routesList(): NetworkRouteList

    suspend fun stoppageList(
        routeIDs: String,
        trip: Int
    ): NetworkStoppage

    suspend fun studentToMarkTransAttendane(
        routeIDs: String,
        stopID: Int,
        trip: Int,
        attDate: String,
        stopIDs: String
    ): NetworkStudentToMarkTransAttendane

    suspend fun postTransAttendance(
        attDate: String,
        routeID: Int,
        stopID: Int,
        stuAtt: List<StuAtt>,
        trip: Int
    ): CommonResponse

    suspend fun studentToDrop(
        routeID : Int,
        stopID: Int,
        attDate: String,
    ): NetworkStudentToMarkTransAttendane

    suspend fun transAttendanceReport(
        routeID : Int,
        stopIDs: String,
        attDate: String,
    ): NetworkTransAttendanceReport

    suspend fun getOutPassReport(
        attDate: String,
    ): NetworkOutPassReport

    suspend fun getStudentListToAssignRollNo(
        iD: String,
        orderby: Int
    ): NetworkAssignRollNo

    suspend fun getClassTeacherOf(  ): NetworkClassTeacherOf

    suspend fun assignRollNumber(
        request: List<AssignRollNoBodyItem>
    ): CommonResponse

    suspend fun dropToStudent(
        stID : Int,
        attDate: String,
        hasDropped: Boolean,
    ): CommonResponse

    suspend fun getAppMsgUses(
        fromDate : String,
        toDate: String,
        iD: String,
    ): NetworkSmsMsgReport

    suspend fun getSMSUses(
        fromDate : String,
        toDate: String,
        iD: String,
    ): NetworkSmsMsgReport

    suspend fun getSMSConsumption(
        fromDate : String,
        toDate: String
    ): NetworkSMSConsumption

    suspend fun getSMSBalnceInfo(  ): NetworkSMSBalnceInfo

    suspend fun getRechargeLog(
        fromDate : String,
        toDate: String
    ): NetworkRechargeLog

    suspend fun getGenerateToken(
        device : Int
    ): NetworkGenerateTokenFeePay


    suspend fun surveyList(
        pg: Int, isReport: Boolean
    ): SurveyListResponse
    suspend fun surveyQuestions(
        id: String
    ): SurveyQuestionsResponse

    suspend fun submitSurveyQuestions(
        request: SurveyQuestionsSubmitRequest
    ): CommonResponse
    suspend fun getQuestionPaper(
         classID: Int,
          yrID: Int
    ): NetworkQuestionPaper

    suspend fun getPhotoAlbumTypes( ): NetworkAlbumType

    suspend fun getPhotoAlbums(
         typeID: Int,
          pg: Int,
    ): NetworkPhotoAlbum


    suspend fun getPhotoAlbumDTL(
          iD: String,
          pg: Int,
    ): NetworkAlbumPhotoDetails

     suspend fun getVideoAlbums(
          pg: Int,
    ): NetworkVideoAlbum

    suspend fun getVideoAlbumDTL(
          id: String,
          pg: Int,
    ): NetworkVideoAlbumDTL


    suspend fun getFavorites(
          pg: Int,
    ): NetworkFavorites

    suspend fun manageFavorites(
          id: String,
         galleryType: Int,
          action: String
    ): CommonResponse

    suspend fun manageLikes(
        id: String,
         galleryType: Int,
         like: Boolean
    ): CommonResponse

    suspend fun getMediaGallery(
          pg: Int,
          queryType: Int,
         year: Int,
          date: String,
         query: String
    ): NetworkMediaGallery

    suspend fun getMyQuestionBank(  ): NetworkQuestionBank

    suspend fun  getQuestionBankCreate(  ): NetworkQuestionBankCreate

    suspend fun getQuestionBankSubject(
         classID: Int
    ): NetworkQuestionBankSubject

    suspend fun getQuestionBankChapters(
         classID: Int,
         subID: Int
    ): NetworkQuestionBankChapters

    suspend fun submitPostQuestion(
          model: NetworkPostQuestionBank
    ): CommonResponse

    suspend fun getDeleteQuestion(
          id: String
    ): CommonResponse

    suspend fun getEBook(
         query: String,
          mode: Int
    ): NetworkEBook

    suspend fun getEBookDetails(
         accessionNo: String
    ): CommonResponse

    suspend fun getTeacherSyllabuses( ): NetworkTeacherSyllabus

    suspend fun saveSyllabus(
          request: PostSyllabus,
    ): CommonResponse

    suspend fun deleteSyllabus(
         ID: String
    ): CommonResponse

    suspend fun getVehicleNumber(  ): NetworkVehicleNumber

    suspend fun busLocation(
          vehicleNumber: String
    ): NetworkBusLocation

}