package com.app.ecarepro.data

import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.AddThoughtsPostData
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
import com.app.ecarepro.data.network.model.NetworkLatestBook
import com.app.ecarepro.data.network.model.NetworkLeaveListStatus
import com.app.ecarepro.data.network.model.NetworkLeaveSetting
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkLessonPlanDTL
import com.app.ecarepro.data.network.model.NetworkLessonPlanList
import com.app.ecarepro.data.network.model.NetworkMarkAttendance
import com.app.ecarepro.data.network.model.NetworkMySubjects
import com.app.ecarepro.data.network.model.NetworkPaySlip
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
import com.app.ecarepro.data.network.model.NetworkUser
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.data.network.model.NetworkWhoLike
import com.app.ecarepro.data.network.model.PostAnswerPostData
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.data.network.model.UserLoginRequestDto
import com.app.ecarepro.data.network.model.asEntity
import com.app.ecarepro.data.network.model.create_assignment.PostCreateAssignment
import com.app.ecarepro.data.network.model.post_leave_request.FileAttachment
import com.app.ecarepro.data.network.model.post_leave_request.HalfdayDTL
import com.app.ecarepro.data.network.model.post_leave_request.LeaveRequestData
import com.app.ecarepro.data.network.model.post_lesson.ActionOnLesson
import com.app.ecarepro.data.network.model.post_lesson.PostLesson
import com.app.ecarepro.data.network.model.post_mark_attedance.PostMarkAttedance
import com.app.ecarepro.data.network.model.post_mark_attedance.StudentAtt
import com.app.ecarepro.data.network.model.post_question.AddQuestionPostData
import com.app.ecarepro.data.network.model.post_question.Attachment
import com.app.ecarepro.data.network.model.post_save_appreaction.PostSaveAppreciation
import com.app.ecarepro.data.network.model.post_save_infraction.PostSaveInfraction
import com.app.ecarepro.data.network.model.submit_assignment.PostSubmitAssignment
import com.app.ecarepro.data.network.service.UserService
import com.app.ecarepro.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import com.app.ecarepro.data.network.model.ChangeUserNameRequestDto
import com.app.ecarepro.data.network.model.NetworkAcademicPerformance
import com.app.ecarepro.data.network.model.NetworkAppreciations
import com.app.ecarepro.data.network.model.NetworkClassTeacher
import com.app.ecarepro.data.network.model.NetworkInfractions
import com.app.ecarepro.data.network.model.NetworkLeaveReport
import com.app.ecarepro.data.network.model.NetworkProfileAttendanceDTL
import com.app.ecarepro.data.network.model.NetworkTimeTableViewer
import com.app.ecarepro.data.network.model.PostLeaveAction
import kotlinx.coroutines.flow.flow
import com.app.ecarepro.data.network.model.Profile
import com.app.ecarepro.data.network.model.UploadPhotoRequest
import com.app.ecarepro.model.FeeSummery
import com.app.ecarepro.ui.award.ExcellenceAwardResponse
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDatabase: UserDatabase,
    private val userService: UserService,
    private val userDataStore: UserDataStore
) : UserRepository {
    override suspend fun insertUser(user: NetworkUser) {
        userDatabase.insertUser(user = user.asEntity())
    }

    override suspend fun verifyUser(schoolCode: String, username: String): NetworkUserDetailsDto {
        return userService.verifyUser(schoolCode, username).also { userDataStore.saveUser(it) }
    }

    override suspend fun getCredentials(
        schoolCode: String,
        userType: Int,
        rcvOn: String,
        mobile: String?,
        email: String?
    ): NetworkUserDetailsDto {
        return userService.getCredentials(
            GetCredentialsRequest(
                email,
                mobile,
                rcvOn,
                schoolCode,
                userType
            )
        )
    }

    override suspend fun login(
        schoolCode: String,
        userName: String,
        password: String
    ): LoginResponseDto {
        return userService.login(
            UserLoginRequestDto(
                schCode = schoolCode,
                username = userName,
                password = password
            )
        ).also {
            if (it.authenticated==true){
                userDataStore.saveAuthToken(it.authToken ?: "")
                userDataStore.setAsUserAuthenticated(it.authenticated ?: false)
                userDataStore.saveUserType(it.userType ?: 0)
                userDataStore.saveRoleName(it.roleName ?: "")
            }

        }
    }
    override suspend fun changeUserName(changeUserNameRequestDto: ChangeUserNameRequestDto): Flow<Result<CommonResponse>> {
        return flow {
            try {
                val checkUserName =
                    userService.checkUsernameAvailability(changeUserNameRequestDto.newUsername!!)
                if (checkUserName.errorCode == 0) {
                    val response = userService.changeUsername(changeUserNameRequestDto)
                    if (response.errorCode == 0) {
                        emit(Result.success(response))
                    } else {
                        emit(Result.failure(IllegalArgumentException(response.message)))
                    }
                } else {
                    emit(Result.failure(IllegalArgumentException(checkUserName.message)))
                }

            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override suspend fun changePassword(
        password: String,
        confirmPassword: String
    ): Flow<Result<CommonResponse>> {
        return flow {
            try {
                val response = userService.changePassword(
                    ChangeUserNameRequestDto(
                        newPassword = password,
                        newUsername = confirmPassword,
                        currentUsername = "SF129"
                    )
                )
                if (response.errorCode == 0) {
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override suspend fun getClassSyllabus(): NetworkClassSyllabus {
        return userService.getClassSyllabus()
    }

    override suspend fun getActivityCalender(): NetworkActivityCalender {
        return userService.getActivityCaledar()
    }

    override suspend fun getLibraryDetails(): NetworkLatestBook {
        return userService.getLibraryDetails()
    }

    override suspend fun getBookDetails(bookID: Int, id: Int): NetworkBookDetails {
        return userService.getBookDetails(bookID, id)
    }

    override suspend fun getLibrarySearch(query: String, pg: Int): NetworkBookDetails {
        return   userService.getLibrarySearch(query, pg)
    }

    override suspend fun getQuestionnaireList(pg: Int, myque: Boolean ): NetworkQuestionnaire {
        return userService.getQuestionnaireList(pg, myque)
    }

    override suspend fun questionnaireLike(qID: Int, like: Boolean): CommonResponse {
        return  userService.questionnaireLike(qID, like)
    }

    override suspend fun answerList(qID: Int): NetworkAnswerDetails {
        return userService.answerList(qID)
    }

    override suspend fun postAnswer(qid: String, answer: String): CommonResponse {
        return userService.postAnswer(PostAnswerPostData(qid, answer))
    }

    override suspend fun deleteAnswer(ansID: Int): CommonResponse {
        return userService.deleteAnswer(ansID)
    }

    override suspend fun addQuestion(
        question: String,
        attachment: String,
        fileURL: String,
        fileExt: String
    ): CommonResponse {
        return userService.addQuestion(AddQuestionPostData(Attachment(attachment, fileExt, fileURL),question) )
    }

    override suspend fun leaveListStatus(): NetworkLeaveListStatus {
       return userService.leaveListStatus()
    }

    override suspend fun leaveReport(
        status: Int,
        ord: Int,
        applType: Int,
        pg: Int
    ): NetworkLeaveReport {
        return userService.leaveReport(status, ord, applType, pg)
    }

    override suspend fun leaveAction(
        applType: Int,
        lvID: Int,
        action: Int,
        forwardedTo: Int
    ): CommonResponse {
        return    userService.leaveAction(PostLeaveAction(action, applType, forwardedTo, lvID))
    }

    override suspend fun leaveApply(
        leaveID: Int,
        fromDate: String,
        tillDate: String,
        duration: Int,
        halfdayDTL: List<HalfdayDTL>,
        reason: String,
        attachment: String,
        fileExt: String
    ): CommonResponse {
         return userService.leaveApply(LeaveRequestData(duration,
             FileAttachment(attachment,fileExt,""),
             fromDate,
             halfdayDTL,
             leaveID,
             reason,
             tillDate
         ))
    }

    override suspend fun leaveSetting(): NetworkLeaveSetting {
        return userService.leaveSetting()
    }

    override suspend fun leaveDelete(lvID: Int): CommonResponse {
       return userService.leaveDelete(lvID)
    }

    override suspend fun getInfractionTypes(): NetworkInfractionTypes {
        return userService.infractionTypes()
    }

    override suspend fun getSubInfractionTypes(infrTypeID: Int): NetworkSubInfractionTypes {
        return userService.subInfractionTypes(infrTypeID)
    }

    override suspend fun infractionInstance(
        infrTypeID: Int,
        InfrSubTypeID: Int,
        InfrTypeID: Int
    ): NetworkInfractionInstance {
        return userService.infractionInstance(infrTypeID, InfrSubTypeID, InfrTypeID)
    }

    override suspend fun addInfraction(stID: Int): NetworkAddInfraction {
        return userService.addInfraction(stID)
    }

    override suspend fun getAppreciations(stID: Int): NetworkAppreciations {
        return userService.getAppreciations(stID)
    }

    override suspend fun getInfractions(stID: Int): NetworkInfractions {
        return userService.getInfractions(stID)
    }

    override suspend fun saveInfraction(
        action: Int,
        stID: Int,
        infrSubTypeID: Int,
        consID: Int,
        instance: Int,
        infractionOn: String,
        correctiveAction: String
    ): CommonResponse {
      return  userService.saveInfraction(PostSaveInfraction(
            action, consID, correctiveAction, infrSubTypeID, infractionOn, instance, stID
        ))
    }

    override suspend fun getStudentList(scholarType: Int, showAll: Boolean): NetworkStudentList {
       return userService.getStudentList(scholarType, showAll)
    }

    override suspend fun addAppreciation(stID: Int): NetworkAddAppreciation {
        return userService.addAppreciation(stID)
    }

    override suspend fun subAppreciationTypes(aprID: Int): NetworkSubAppreciationTypes {
        return userService.subAppreciationTypes(aprID)
    }

    override suspend fun appreciationInstance(
        aprSubID: Int,
        stID: Int
    ): NetworkAppreciationInstance {
        return userService.appreciationInstance(aprSubID, stID)
    }

    override suspend fun saveAppreciation(
        action: Int,
        stID: Int,
        aprSubID: Int,
        rwdID: Int,
        instance: Int,
        appreciationOn: String,
        remark: String
    ): CommonResponse {
        return userService.saveAppreciation(PostSaveAppreciation(action, appreciationOn, aprSubID, instance, remark, rwdID, stID))
    }

    override suspend fun assignment(): NetworkAssignments {
        return userService.assignment()
    }

    override suspend fun submitAssignment(
        id: String,
        asgID: Int,
        data: String,
        fileName: String,
        attachment: String,
        fileURL: String,
        fileExt: String
    ): CommonResponse {
        return userService.submitAssignment(PostSubmitAssignment(asgID,
            com.app.ecarepro.data.network.model.submit_assignment.Attachment(attachment,fileExt, fileURL),
            data, fileName, id
        ))
    }

    override suspend fun teachersAssignment(iD: String,): NetworkTeacherAssignment {
        return userService.teachersAssignment(iD)
    }

    override suspend fun deleteAssignment(iD: String): CommonResponse {
        return userService.deleteAssignment(iD)
    }

    override suspend fun mySubjects(classID :Int): NetworkMySubjects {
        return userService.mySubjects(classID)
    }

    override suspend fun createAssignment(
          asgDate: String,
          asgID: Int,
          attachment: String,
          fileExt: String,
          fileURL: String,
          classID: Int,
          classIDs: String,
          `data`: String,
          `file`: String,
          id: String,
          isActive: Boolean,
          isFileRemoved: Boolean,
          multipleSubmission: Boolean,

          subjectID: Int,
          submitDate: String,
          title: String
    ): CommonResponse {
        return userService.createAssignment(PostCreateAssignment( asgDate, asgID, Attachment(attachment, fileExt, fileURL), classID, classIDs, data, file, id, isActive, isFileRemoved, multipleSubmission, subjectID, submitDate, title))

    }

    override suspend fun postMarkAttedance(
        classID: Int,
        subID: Int,
        mode: Int,
        attDate: String,
        stuList: List<StudentAtt>
    ): CommonResponse {
        return userService.postMarkAttendance(PostMarkAttedance(attDate,classID,mode,stuList,subID))
    }

    override suspend fun getLessonPlanList(page : Int,id: String): NetworkLessonPlanList {
        return  userService.getLessonPlanList(page,id)
    }

    override suspend fun getLessonPlanFilter(
        filter: String,
        from: String,
        till: String,
        classIds: String,
        subIds: String,
        status: Int,

    ): NetworkLessonPlanList {
        return userService.getLessonPlanFilter(filter, from, till, classIds, subIds, status )
    }

    override suspend fun getLessonPlanDTL(id: String, teacherID: Int): NetworkLessonPlanDTL {
        return userService.getLessonPlanDTL(id, teacherID)
    }

    override suspend fun getStaffList(): NetworkStaffList {
        return  userService.getStaffList()
    }

    override suspend fun getStaffProfile(sId: Int): NetworkStaffProfile {
        return userService.getStaffProfile(sId)
    }

    override suspend fun postLessonPlan(
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
    ): CommonResponse {

        return userService.postLessonPlan(PostLesson(
            com.app.ecarepro.data.network.model.post_lesson.Attachment(attachment, fileExt, fileURL),
            auditory, classIds, closure, extensionTopic, fileName, fromDate, introduction, kinestheticActivity, lPlnID, learningOutcomes, objective, otherResources, resources, showToStudent, subID, tillDate, topic, youtubeLinks
        ))
    }

    override suspend fun createLessonPlan(): NetworkCreateLesson {
        return userService.createLessonPlan()
    }

    override suspend fun getStudentProfile(sId: Int): NetworkStudentProfile {
        return userService.getStudentProfile(sId)
    }

    override suspend fun getSAttendanceYrID(sId: Int, yrID: Int): NetworkProfileAttendanceDTL {
        return userService.getSAttendanceYrID(sId, yrID)
    }

    override suspend fun getFeeSummaryYrID(sId: Int, yrID: Int): FeeSummery {
        return userService.getFeeSummaryYrID(sId, yrID)
    }

    override suspend fun getAcademicPerformance(sId: Int, yrID: Int): NetworkAcademicPerformance {
        return userService.getAcademicPerformance(sId, yrID)
    }

    override suspend fun getAttendanceSummary(attDate: String): NetworkAttedanceSummary {
        return userService.getAttendanceSummary(attDate)
    }

    override suspend fun getClassAttendance(id: String, attDate: String): NetworkClassAttendance {
        return userService.getClassAttendance(id, attDate)
    }

    override suspend fun getStudentAttendance(
        from: String,
        till: String,
        yrID: String,
        iD: String
    ): NetworkStudentAttRepo {
        return userService.getStudentAttendance(from, till, yrID, iD)
    }

    override suspend fun lessonPlanAction(
        lPlnID: Int,
        action: Int,
        rejectionComments: String
    ): CommonResponse {
        return userService.lessonPlanAction(ActionOnLesson(action, lPlnID, rejectionComments))
    }

    override suspend fun getClassTeacher(): NetworkClassTeacher {
        return userService.getClassTeacher()
    }

    override suspend fun getTimetableViewer(): NetworkTimeTableViewer {
        return userService.getTimetableViewer()
    }

    override suspend fun viewAssignment(iD: String): NetworkViewAssignment {
       return userService.viewAssignment(iD)
    }

    override suspend fun assignmnetSubmissionRPT(
        iD: String,
        notSubmitted: Boolean
    ): NetworkSubmitAssignReport {
        return userService.assignmnetSubmissionRPT(iD, notSubmitted)
    }

    override suspend fun offlineSubmited(
        iD: String,
        stID: Int,
        submissitedOn: String
    ): CommonResponse {
        return userService.offlineSubmited(iD, stID, submissitedOn)
    }

    override suspend fun staffAttendance(month: Int, year: Int): NetworkStaffAttendence {
        return userService.staffAttendance(month, year)
    }

    override suspend fun teachersTimetable(id: String): NetworkTeachersTimetable {
        return userService.teachersTimetable(id)
    }

    override suspend fun birthday(userType: Int, rptType: Int, monthNo: Int, date:String): NetworkBirthday {
        return userService.birthday(userType, rptType, monthNo,date)
    }

    override suspend fun reportCardDTL(stID: Int): NetworkReportCardDetails {
        return userService.reportCardDTL(stID)
    }

    override suspend fun markAttendance(): NetworkMarkAttendance {
        return userService.markAttendance()
    }

    override suspend fun getStudentListToMarkAtt(
        classID: Int,
        subID: Int,
        attDate: String
    ): NetworkStudentListToMarkAtt {
        return userService.getStudentListToMarkAtt(classID, subID, attDate)
    }


    override fun getUserProfile(): Flow<Result<Profile>> {
        return flow {
            try {
                val response = userService.getUserProfile()
                if (response.errorCode == 0) {
                    emit(Result.success(response.profile))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }
    override fun uploadProfileIMG(uploadPhotoRequest: UploadPhotoRequest): Flow<Result<String>> {
        return flow {
            try {
                val response = userService.uploadProfileIMG(uploadPhotoRequest)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Success"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }
    override suspend fun staffMyClass(subID: Int, iD: Int): NetworkMyClass {
        return  userService.staffMyClass()
    }

    override suspend fun getPayslip(): NetworkPaySlip {
        return userService.getPayslip()
    }

    override suspend fun getThoughts(pg: Int, dir: Int, mythoughts: Boolean): NetworkThoughts {
        return  userService.getThoughts(pg, dir, mythoughts)
    }

    override suspend fun thoughtsLike(thID: Int, like: Boolean): CommonResponse {
        return userService.thoughtsLike(thID, like)
    }

    override suspend fun thoughtsDelete(thID: Int): CommonResponse {
        return userService.thoughtsDelete(thID )
    }

    override suspend fun whoLiked(thID: Int): NetworkWhoLike {
        return  userService.whoLiked(thID)
    }

    override suspend fun thoughtsCreate(quotation: String, author: String): CommonResponse {
        return userService.thoughtsCreate(AddThoughtsPostData(quotation, author))
    }
    override suspend fun excellenceAward(): ExcellenceAwardResponse {
        return userService.excellenceAward()
    }

    override fun getUserDashboard(): Flow<Result<UserDashboardDto>> {
        return flow {
            try {
                val response = userService.getUserDashboard()
                if (response.errorCode == 0) {
                    userDataStore.saveDashboardData(response)
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }
}