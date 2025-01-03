package com.app.ecarepro.data

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings.Secure
import com.app.ecarepro.AssignHouseRequest
import com.app.ecarepro.data.cache.JsonCache
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.CreateUserSessionRequestDto
import com.app.ecarepro.data.network.UserSessionResponseDto
import com.app.ecarepro.data.network.model.AddThoughtsPostData
import com.app.ecarepro.data.network.model.ChangeUserNameRequestDto
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Department
import com.app.ecarepro.data.network.model.Designation
import com.app.ecarepro.data.network.model.Employee
import com.app.ecarepro.data.network.model.FeeCollection
import com.app.ecarepro.data.network.model.Form
import com.app.ecarepro.data.network.model.GetCredentialsRequest
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkAcademicPerformance
import com.app.ecarepro.data.network.model.NetworkAcademicYear
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkAddAppreciation
import com.app.ecarepro.data.network.model.NetworkAddInfraction
import com.app.ecarepro.data.network.model.NetworkAlbumPhotoDetails
import com.app.ecarepro.data.network.model.NetworkAlbumType
import com.app.ecarepro.data.network.model.NetworkAnswerDetails
import com.app.ecarepro.data.network.model.NetworkAppointments
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
import com.app.ecarepro.data.network.model.NetworkSection
import com.app.ecarepro.data.network.model.NetworkSmsMsgReport
import com.app.ecarepro.data.network.model.NetworkSmsReportDetails
import com.app.ecarepro.data.network.model.NetworkSmsReportModel
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
import com.app.ecarepro.data.network.model.NetworkWingReport
import com.app.ecarepro.data.network.model.PostAnswerPostData
import com.app.ecarepro.data.network.model.PostLeaveAction
import com.app.ecarepro.data.network.model.Profile
import com.app.ecarepro.data.network.model.Purpose
import com.app.ecarepro.data.network.model.StaffAttendanceDetails
import com.app.ecarepro.data.network.model.StudentPhotoUploadModel
import com.app.ecarepro.data.network.model.UploadPhotoRequest
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.data.network.model.UserLoginRequestDto
import com.app.ecarepro.data.network.model.UserProfileDto
import com.app.ecarepro.data.network.model.UserUndertakingModule
import com.app.ecarepro.data.network.model.ValidateOtpRequest
import com.app.ecarepro.data.network.model.VisitorDetails
import com.app.ecarepro.data.network.model.create_assignment.AssignmentRemarkPost
import com.app.ecarepro.data.network.model.create_assignment.PostCreateAssignment
import com.app.ecarepro.data.network.model.create_syllabus.PostSyllabus
import com.app.ecarepro.data.network.model.postQuestionBank.NetworkPostQuestionBank
import com.app.ecarepro.data.network.model.post_leave_request.FileAttachment
import com.app.ecarepro.data.network.model.post_leave_request.HalfdayDTL
import com.app.ecarepro.data.network.model.post_leave_request.LeaveRequestData
import com.app.ecarepro.data.network.model.post_lesson.ActionOnLesson
import com.app.ecarepro.data.network.model.post_lesson.PostLesson
import com.app.ecarepro.data.network.model.post_mark_attedance.PostMarkAttedance
import com.app.ecarepro.data.network.model.post_mark_attedance.StudentAtt
import com.app.ecarepro.data.network.model.post_question.AddQuestionPostData
import com.app.ecarepro.data.network.model.post_question.Attachment
import com.app.ecarepro.data.network.model.post_roll_no.AssignRollNoBodyItem
import com.app.ecarepro.data.network.model.post_save_appreaction.PostSaveAppreciation
import com.app.ecarepro.data.network.model.post_save_infraction.PostSaveInfraction
import com.app.ecarepro.data.network.model.post_trans_att.PostStudentToMarkAtt
import com.app.ecarepro.data.network.model.post_trans_att.StuAtt
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankChapters
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankCreate
import com.app.ecarepro.data.network.model.question_bank.NetworkQuestionBankSubject
import com.app.ecarepro.data.network.model.submit_assignment.PostSubmitAssignment
import com.app.ecarepro.data.network.model.submit_assignment.TwoFactorLoginResponseDto
import com.app.ecarepro.data.network.model.submit_assignment.UserDTL
import com.app.ecarepro.data.network.service.UserService
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.ClassID_StID
import com.app.ecarepro.model.ClassMateResponse
import com.app.ecarepro.model.FeeSummery
import com.app.ecarepro.model.Staff
import com.app.ecarepro.model.Student
import com.app.ecarepro.model.StudentTeacherResponse
import com.app.ecarepro.ui.appuserreport.AppUserReportResponse
import com.app.ecarepro.ui.appuserreport.AppUserWebResponse
import com.app.ecarepro.ui.attendance_section.AttendanceResponse
import com.app.ecarepro.ui.award.ExcellenceAwardResponse
import com.app.ecarepro.ui.dashbord.model.ModeWiseCollection
import com.app.ecarepro.ui.edit_profile.model.update_profile.UpdateProfileModel
import com.app.ecarepro.ui.medicalcard.medical_class.StudentMedicalCardResponse
import com.app.ecarepro.ui.medicine_issue.MedicineIsuueModel
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import com.app.ecarepro.ui.statical.StaticGraphResponse
import com.app.ecarepro.ui.studentId.StudentCardResponse
import com.app.ecarepro.ui.studentId.StudentIDRequest
import com.app.ecarepro.ui.survey.SurveyListResponse
import com.app.ecarepro.ui.survey.SurveyQuestionsResponse
import com.app.ecarepro.ui.survey.SurveyQuestionsSubmitRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val userService: UserService,
    private val userDataStore: UserDataStore,
    private val jsonCache: JsonCache
) : UserRepository {


    override suspend fun verifyUser(schoolCode: String, username: String): NetworkUserDetailsDto {
        return userService.verifyUser(schoolCode, username)
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

    override suspend fun forgotPassword(
        SchCode: String,
        UserID: String,
        UserType: String,
        RcvOn: String
    ): NetworkUserDetailsDto {
        return userService.forgotPassword(SchCode, UserID, UserType, RcvOn)
    }

    fun getCurrentDateTimeAmPm(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(currentDate)
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
                password = password,
            )
        ).also {
            if (it.authenticated == true) {
                userDataStore.saveUserDetails(it, schoolCode, getCurrentDateTimeAmPm())
                userDataStore.saveAuthToken(it.authToken ?: "")
                userDataStore.setAsUserAuthenticated(it.authenticated)
                userDataStore.saveUserType(it.userType ?: 0)

                userDataStore.saveRoleName(it.roleName ?: "")
                userDataStore.saveUserNameID(userName ?: "")
            }

        }
    }

    override suspend fun twoFactorLogin(
        schoolCode: String,
        userName: String,
        password: String
    ): TwoFactorLoginResponseDto {
        return userService.twoFactorLogin(
            UserLoginRequestDto(
                schCode = schoolCode,
                username = userName,
                password = password,
                deviceInfo = CreateUserSessionRequestDto(
                    ipAddress = Secure.getString(
                        context.contentResolver,
                        Secure.ANDROID_ID
                    ),
                    locationCity = userDataStore.getCityName(),
                )
            )
        ).also {
            if (it.authenticated == true && it.isOTPEnabled == false) {
                it.userDTL?.let { userDtl: UserDTL ->
                    saveUserDtl(userDtl, schoolCode, userName)
                }
            }
        }
    }

    override fun createSession(regenerate: Boolean): Flow<Result<UserSessionResponseDto>> {
        return flow {
            try {
                val response = userService.createSession(
                    CreateUserSessionRequestDto(
                        ipAddress = Secure.getString(
                            context.contentResolver,
                            Secure.ANDROID_ID
                        ),
                        locationCity = userDataStore.getCityName(),
                        oldSessionID = if (regenerate) userDataStore.getUserSessionId() else null
                    )
                )
                if (response.errorCode == 0) {
                    userDataStore.saveSessionId(response.sessionID)
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override suspend fun resendOtp(
        schoolCode: String,
        oTPAuthKey: String,
    ): Flow<Result<TwoFactorLoginResponseDto>> {
        return flow {
            try {
                val response =
                    userService.resendOTP(
                        ValidateOtpRequest(
                            schCode = schoolCode,
                            oTPAuthKey = oTPAuthKey
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

    override suspend fun validateOtp(
        schoolCode: String,
        oTPAuthKey: String,
        otp: String,
        userName: String
    ): Flow<Result<TwoFactorLoginResponseDto>> {
        return flow {
            try {
                val response =
                    userService.validateOTP(
                        ValidateOtpRequest(
                            schCode = schoolCode,
                            oTPAuthKey = oTPAuthKey,
                            otp = otp
                        )
                    )
                if (response.errorCode == 0) {
                    response.also {
                        it.userDTL?.let { userDtl: UserDTL ->
                            saveUserDtl(userDtl, schoolCode, userName)
                        }
                    }
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    private suspend fun saveUserDtl(
        userDtl: UserDTL,
        schoolCode: String,
        userName: String
    ) {
        userDataStore.saveUserDetails(userDtl, schoolCode, getCurrentDateTimeAmPm())
        userDataStore.saveAuthToken(userDtl.authToken ?: "")
        userDataStore.setAsUserAuthenticated(userDtl.authenticated ?: false)
        userDataStore.saveUserType(userDtl.userType ?: 0)
        userDataStore.saveRoleName(userDtl.roleName ?: "")
        userDataStore.saveUserNameID(userName ?: "")
    }

    override suspend fun logout(): Flow<Result<Boolean>> {
        return flow {
            try {
                val response = userService.logout(
                    deviceID = Secure.getString(
                        context.contentResolver,
                        Secure.ANDROID_ID
                    ),
                    sessionID = userDataStore.getUserSessionId().orEmpty()
                )
                if (response.errorCode == 0) {
                    emit(Result.success(true))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
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
                        currentUsername = ""
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

    override suspend fun getLibraryDTL(): NetworkLibraryDTL {
        return userService.getLibraryDTL()
    }


    override suspend fun getBookDetails(bookID: Int, id: Int): NetworkBookDetails {
        return userService.getBookDetails(bookID, id)
    }

    override suspend fun getLibrarySearch(query: String, pg: Int): NetworkBookDetails {
        return userService.getLibrarySearch(query, pg)
    }

    override suspend fun getQuestionnaireList(pg: Int, myque: Boolean): NetworkQuestionnaire {
        return userService.getQuestionnaireList(pg, myque)
    }

    override suspend fun questionnaireLike(qID: Int, like: Boolean): CommonResponse {
        return userService.questionnaireLike(qID, like)
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

    override suspend fun deleteQID(QID: Int): CommonResponse {
        return userService.deleteQID(QID)
    }

    override suspend fun addQuestion(
        question: String,
        attachment: String,
        fileURL: String,
        fileExt: String
    ): CommonResponse {
        return userService.addQuestion(
            AddQuestionPostData(
                Attachment(attachment, fileExt, fileURL),
                question
            )
        )
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
        lvID: Int?,
        lvIDs: String?,
        action: Int,
        forwardedTo: Int,
        rejectionReason: String,
        isPartialApproved: Boolean?,
        partialFromDate: String?,
        partialTillDate: String?,
    ): CommonResponse {
        return userService.leaveAction(
            PostLeaveAction(
                action,
                applType,
                forwardedTo,
                lvID,
                lvIDs,
                rejectionReason,
                isPartialApproved,
                partialFromDate,
                partialTillDate
            )
        )
    }

    override suspend fun medicineIsuueModel(): MedicineIsuueModel {
        return userService.medicineIssued()
    }

    override suspend fun leaveApply(
        leaveID: Int,
        fromDate: String,
        tillDate: String,
        duration: Double,
        halfdayDTL: List<HalfdayDTL>?,
        reason: String,
        fileAttachment: FileAttachment?,

        ): CommonResponse {
        return userService.leaveApply(
            LeaveRequestData(
                duration,
                fileAttachment,
                fromDate,
                halfdayDTL,
                leaveID,
                reason,
                tillDate
            )
        )
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
        stID: Int
    ): NetworkInfractionInstance {
        return userService.infractionInstance(infrTypeID, InfrSubTypeID, stID)
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

    override suspend fun disciplineLogDeleteLog(id: String, type: Int): CommonResponse {
        return userService.disciplineLogDeleteLog(id, type)
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
        return userService.saveInfraction(
            PostSaveInfraction(
                action, consID, correctiveAction, infrSubTypeID, infractionOn, instance, stID
            )
        )
    }

    override suspend fun getStudentList(scholarType: Int, showAll: Boolean): NetworkStudentList {
        return userService.getStudentList(scholarType, showAll)
    }

    override suspend fun getStudentMedicalCard(stID: String): StudentMedicalCardResponse {
        return userService.getStudentMedicalCard(stID)
    }

    override suspend fun getStudentTeachers(): StudentTeacherResponse {
        return userService.getStudentTeachers()
    }

    override suspend fun getClassmates(): ClassMateResponse {
        return userService.getClassmates()
    }

    override suspend fun uploadPhoto(request: StudentIDRequest): CommonResponse {
        return userService.uploadPhoto(request)
    }

    override suspend fun getStudentIDCard(): StudentCardResponse {
        return userService.getStudentIDCard()
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
        return userService.saveAppreciation(
            PostSaveAppreciation(
                action,
                appreciationOn,
                aprSubID,
                instance,
                remark,
                rwdID,
                stID
            )
        )
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
        return userService.submitAssignment(
            PostSubmitAssignment(
                asgID,
                com.app.ecarepro.data.network.model.submit_assignment.Attachment(
                    attachment,
                    fileExt,
                    fileURL
                ),
                data, fileName, id
            )
        )
    }

    override suspend fun teachersAssignment(iD: String): NetworkTeacherAssignment {
        return userService.teachersAssignment(iD)
    }

    override suspend fun deleteAssignment(iD: String): CommonResponse {
        return userService.deleteAssignment(iD)
    }

    override suspend fun mySubjects(classID: Int): NetworkMySubjects {
        return userService.mySubjects(classID)
    }

    override suspend fun staffSubjects(classSTD: Int): NetworkMySubjects {
        return userService.staffSubjects(classSTD)
    }

    override suspend fun createAssignment(
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
    ): CommonResponse {
        return userService.createAssignment(
            PostCreateAssignment(
                asgDate,
                asgID,
                classID,
                classIDs,
                data,
                file,
                id,
                isActive,
                isFileRemoved,
                multipleSubmission,
                subjectID,
                submitDate,
                title,
                lateSubmission,
                attachments,
                classID_StID,
                stIDs
            )
        )

    }

    override suspend fun getStaffProfileMain(sId: Int): NetworkStaffProfile {
        return userService.getStaffProfileMain(sId)
    }

    override suspend fun postMarkAttedance(
        classID: Int,
        subID: Int,
        mode: Int,
        attDate: String,
        stuList: List<StudentAtt>
    ): CommonResponse {
        return userService.postMarkAttendance(
            PostMarkAttedance(
                attDate,
                classID,
                mode,
                stuList,
                subID
            )
        )
    }

    override suspend fun getLessonPlanList(page: Int, id: String): NetworkLessonPlanList {
        return userService.getLessonPlanList(page, id)
    }

    override suspend fun getLessonPlanFilter(
        filter: String,
        from: String,
        till: String,
        classIds: String,
        subIds: String,
        status: Int,

        ): NetworkLessonPlanList {
        return userService.getLessonPlanFilter(filter, from, till, classIds, subIds, status)
    }

    override suspend fun getLessonPlanDTL(id: String, teacherID: Int): NetworkLessonPlanDTL {
        return userService.getLessonPlanDTL(id, teacherID)
    }

    override suspend fun getStaffList(): NetworkStaffList {
        return userService.getStaffList()
    }

    override suspend fun getStaffAttendance(
        staffType: String?,
        date: String,
    ): Flow<Result<List<StaffAttendanceDetails>>> {
        return flow {
            try {
                val response = userService.staffAttendance(staffType, date)
                if (response.errorCode == 0) {
                    emit(Result.success(response.dtl ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
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

        return userService.postLessonPlan(
            PostLesson(
                com.app.ecarepro.data.network.model.post_lesson.Attachment(
                    attachment,
                    fileExt,
                    fileURL
                ),
                auditory,
                classIds,
                closure,
                extensionTopic,
                fileName,
                fromDate,
                introduction,
                kinestheticActivity,
                lPlnID,
                learningOutcomes,
                objective,
                otherResources,
                resources,
                showToStudent,
                subID,
                tillDate,
                topic,
                youtubeLinks
            )
        )
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

    override suspend fun getStudents(): Flow<Result<List<Student>>> {
        return flow {
            try {
                val response = userService.getStudentListSerch(2)
                if (response.errorCode == 0) {
                    emit(Result.success(response.students))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override suspend fun getStaffs(): Flow<Result<List<Staff>>> {
        return flow {
            try {
                val response = userService.getStaffList()
                if (response.errorCode == 0) {
                    emit(Result.success(response.staffs))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
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

    override suspend fun getClassAssignment(id: String): NetworkAssignments {
        return userService.getClassAssignment(id)
    }

    override suspend fun appointmentOverview(
        appDate: String,
        tillDate: String,
        all: Boolean
    ): NetworkAppointments {
        return userService.appointmentOverview(appDate, tillDate, all)
    }

    override suspend fun appointmentAction(act: Int, appId: Int): CommonResponse {
        return userService.appointmentAction(act, appId)
    }

    override suspend fun routesList(): NetworkRouteList {
        return userService.routesList()
    }

    override suspend fun stoppageList(routeIDs: String, trip: Int): NetworkStoppage {
        return userService.stoppageList(routeIDs, trip)
    }

    override suspend fun studentToMarkTransAttendane(
        routeIDs: String,
        stopID: Int,
        trip: Int,
        attDate: String,
        stopIDs: String
    ): NetworkStudentToMarkTransAttendane {
        return userService.studentToMarkTransAttendane(routeIDs, stopID, trip, attDate, stopIDs)
    }

    override suspend fun postTransAttendance(
        attDate: String,
        routeID: Int,
        stopID: Int,
        stuAtt: List<StuAtt>,
        trip: Int
    ): CommonResponse {
        return userService.postTransAttendance(
            PostStudentToMarkAtt(
                attDate,
                routeID,
                stopID,
                stuAtt,
                trip
            )
        )
    }

    override suspend fun studentToDrop(
        routeID: Int,
        stopID: Int,
        attDate: String
    ): NetworkStudentToMarkTransAttendane {
        return userService.studentToDrop(routeID, stopID, attDate)
    }

    override suspend fun transAttendanceReport(
        routeID: Int,
        StopIds: String,
        attDate: String
    ): NetworkTransAttendanceReport {
        return userService.transAttendanceReport(routeID, StopIds, attDate)
    }

    override suspend fun getOutPassReport(attDate: String): NetworkOutPassReport {
        return userService.getOutPassReport(attDate)
    }

    override suspend fun getStudentListToAssignRollNo(
        iD: String,
        orderby: Int
    ): NetworkAssignRollNo {
        return userService.getStudentListToAssignRollNo(iD, orderby)
    }

    override suspend fun getClassTeacherOf(): NetworkClassTeacherOf {
        return userService.getClassTeacherOf()
    }

    override suspend fun assignRollNumber(request: List<AssignRollNoBodyItem>): CommonResponse {
        return userService.assignRollNumber(request)
    }

    override suspend fun dropToStudent(
        stID: Int,
        attDate: String,
        hasDropped: Boolean
    ): CommonResponse {
        return userService.dropToStudent(stID, attDate, hasDropped)
    }

    override suspend fun getAppMsgUses(
        fromDate: String,
        toDate: String,
        iD: String
    ): NetworkSmsMsgReport {
        return userService.getAppMsgUses(fromDate, toDate, iD)
    }

    override suspend fun getSMSUses(
        fromDate: String,
        toDate: String,
        iD: String
    ): NetworkSmsMsgReport {
        return userService.getSMSUses(fromDate, toDate, iD)
    }

    override suspend fun getSMSType(): NetworkSmsReportModel {
        return userService.getSMSType()
    }

    override suspend fun getSMSReport(
        fromDate: String,
        toDate: String,
        sMSTypeD: Int,
        page: Int
    ): NetworkSmsReportDetails {
        return userService.getSMSReport(fromDate, toDate, sMSTypeD, page)
    }


    override suspend fun getSMSConsumption(
        fromDate: String,
        toDate: String
    ): NetworkSMSConsumption {
        return userService.getSMSConsumption(fromDate, toDate)
    }

    override suspend fun getSMSBalnceInfo(): NetworkSMSBalnceInfo {
        return userService.getSMSBalnceInfo()
    }

    override suspend fun getRechargeLog(fromDate: String, toDate: String): NetworkRechargeLog {
        return userService.getRechargeLog(fromDate, toDate)
    }

    override suspend fun getGenerateToken(device: Int): NetworkGenerateTokenFeePay {
        return userService.getGenerateToken(device)
    }


    override suspend fun viewAssignment(iD: String): NetworkViewAssignment {
        return userService.viewAssignment(iD)
    }

    override suspend fun assignmentDTL(iD: String): NetworkViewAssignment {
        return userService.assignmentDTL(iD)
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

    override suspend fun statistical(): StaticGraphResponse {
        return userService.statistical()
    }

    override suspend fun appUserReportResponse(): AppUserReportResponse {
        return userService.appUsersCount()
    }

    override suspend fun appUserReportWevResponse(userType: String): AppUserWebResponse {
        return userService.appUsersWeb(userType)
    }

    override suspend fun getAttendance(
        from: String,
        till: String,
        yrID: String,
        ID: String
    ): AttendanceResponse {
        return userService.getAttendance(from, till, yrID, ID)
    }

    override suspend fun teachersTimetable(id: String): NetworkTeachersTimetable {
        return userService.teachersTimetable(id)
    }

    override suspend fun classTimetable(id: String?): NetworkTeachersTimetable {
        return userService.classTimetable(id)
    }

    override suspend fun birthday(
        userType: Int,
        rptType: Int,
        monthNo: Int,
        date: String
    ): NetworkBirthday {
        return userService.birthday(userType, rptType, monthNo, date)
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


    override fun getUserProfile(refresh: Boolean): Flow<Result<Profile>> {
        return flow {
            try {
                val response =
                    if (refresh.not() && jsonCache.isCacheAvailable(USER_PROFILE_KEY)) {
                        jsonCache.retrieve(USER_PROFILE_KEY, UserProfileDto::class.java)
                    } else {
                        userService.getUserProfile().also {
                            jsonCache.store(USER_PROFILE_KEY, it)
                        }
                    }
                if (response?.errorCode == 0) {
                    emit(Result.success(response.profile.copy(canEditProfile = response.canEditProfile)))
                } else {
                    emit(Result.failure(IllegalArgumentException(response?.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override suspend fun getUserProfileEdit(edit: Boolean): NetworkEditProfile {
        return userService.getUserProfileEdit(edit)
    }

    override suspend fun updateParentProfile(request: UpdateProfileModel): CommonResponse {
        return userService.updateParentProfile(request)
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
        return userService.staffMyClass()
    }

    override suspend fun staffMyClass(subID: Int, onlyClass: Boolean): NetworkMyClass {
        return userService.staffMyClass(subID, onlyClass)
    }

    override suspend fun getClassSection(classID: Int): NetworkSection {
        return userService.getClassSection(classID)
    }

    override suspend fun getPayslip(): NetworkPaySlip {
        return userService.getPayslip()
    }

    override suspend fun getThoughts(pg: Int, dir: Int, mythoughts: Boolean): NetworkThoughts {
        return userService.getThoughts(pg, dir, mythoughts)
    }

    override suspend fun thoughtsLike(thID: Int, like: Boolean): CommonResponse {
        return userService.thoughtsLike(thID, like)
    }

    override suspend fun thoughtsDelete(thID: Int): CommonResponse {
        return userService.thoughtsDelete(thID)
    }

    override suspend fun whoLiked(thID: Int): NetworkWhoLike {
        return userService.whoLiked(thID)
    }

    override suspend fun thoughtsCreate(quotation: String, author: String): CommonResponse {
        return userService.thoughtsCreate(AddThoughtsPostData(quotation, author))
    }

    override suspend fun excellenceAward(): ExcellenceAwardResponse {
        return userService.excellenceAward()
    }

    override fun getUserDashboard(refresh: Boolean): Flow<Result<UserDashboardDto>> {
        return flow {
            try {
                val response =
                    if (refresh.not() && jsonCache.isCacheAvailable(USER_DASHBOARD_KEY)) {
                        jsonCache.retrieve(USER_DASHBOARD_KEY, UserDashboardDto::class.java)
                    } else {
                        userService.getUserDashboard().also {
                            jsonCache.store(USER_DASHBOARD_KEY, it)
                        }
                    }
                if (response?.errorCode == 0) {
                    userDataStore.saveDashboardData(response)
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response?.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getStudentListToAssignHouse(
        id: String,
        orderBy: String
    ): Flow<Result<UserDashboardDto>> {
        return flow {
            try {
                val response = userService.getStudentListToAssignHouse(id, orderBy)
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

    override fun assignHouse(request: AssignHouseRequest): Flow<Result<CommonResponse>> {
        return flow {
            try {
                val response = userService.assignHouse(request)
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

    override fun getUserUndertaking(refresh: Boolean): Flow<Result<String>> {
        return flow {
            try {
                val response =
                    if (refresh.not() && jsonCache.isCacheAvailable(USER_UNDERTAKING_KEY)) {
                        jsonCache.retrieve(USER_UNDERTAKING_KEY, String::class.java)
                    } else {
                        userService.getUserUndertaking().also {
                            jsonCache.store(USER_UNDERTAKING_KEY, it)
                        }
                    }
                emit(Result.success(response ?: ""))
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun saveUserUndertaking(request: UserUndertakingModule): Flow<Result<String>> {
        return flow {
            try {
                val response = userService.saveUserUndertaking(request)
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

    override suspend fun getQuestionPaper(classID: Int, yrID: Int): NetworkQuestionPaper {
        return userService.getQuestionPaper(classID, yrID)
    }

    override suspend fun getPhotoAlbumTypes(): NetworkAlbumType {
        return userService.getPhotoAlbumTypes()
    }

    override suspend fun getPhotoAlbums(typeID: Int, pg: Int): NetworkPhotoAlbum {
        return userService.getPhotoAlbums(typeID, pg)
    }

    override suspend fun getPhotoAlbumDTL(iD: String, pg: Int): NetworkAlbumPhotoDetails {
        return userService.getPhotoAlbumDTL(iD, pg)
    }

    override suspend fun getVideoAlbums(pg: Int): NetworkVideoAlbum {
        return userService.getVideoAlbums(pg)
    }

    override suspend fun getVideoAlbumDTL(id: String, pg: Int): NetworkVideoAlbumDTL {
        return userService.getVideoAlbumDTL(id, pg)
    }

    override suspend fun getFavorites(pg: Int): NetworkFavorites {
        return userService.getFavorites(pg)
    }

    override suspend fun manageFavorites(
        id: String,
        galleryType: Int,
        action: String
    ): CommonResponse {
        return userService.manageFavorites(id, galleryType, action)
    }

    override suspend fun manageLikes(id: String, galleryType: Int, like: Boolean): CommonResponse {

        return userService.manageLikes(id, galleryType, like)
    }

    override suspend fun getMediaGallery(
        pg: Int,
        queryType: Int,
        year: Int,
        date: String,
        query: String
    ): NetworkMediaGallery {
        return userService.getMediaGallery(pg, queryType, year, date, query)
    }

    override suspend fun getMyQuestionBank(): NetworkQuestionBank {
        return userService.getMyQuestionBank()
    }

    override suspend fun getQuestionBankCreate(): NetworkQuestionBankCreate {
        return userService.getQuestionBankCreate()
    }

    override suspend fun getQuestionBankSubject(classID: Int): NetworkQuestionBankSubject {
        return userService.getQuestionBankSubject(classID)
    }

    override suspend fun getQuestionBankChapters(
        classID: Int,
        subID: Int
    ): NetworkQuestionBankChapters {
        return userService.getQuestionBankChapters(classID, subID)
    }

    override suspend fun submitPostQuestion(model: NetworkPostQuestionBank): CommonResponse {
        return userService.submitPostQuestion(model)
    }

    override suspend fun getDeleteQuestion(id: String): CommonResponse {
        return userService.getDeleteQuestion(id)
    }

    override suspend fun getEBook(query: String, mode: Int): NetworkEBook {
        return userService.getEBook(query, mode)
    }

    override suspend fun getEBookDetails(accessionNo: String): CommonResponse {
        return userService.getEBookDetails(accessionNo)
    }

    override suspend fun getTeacherSyllabuses(): NetworkTeacherSyllabus {
        return userService.getTeacherSyllabuses()
    }

    override suspend fun saveSyllabus(request: PostSyllabus): CommonResponse {
        return userService.saveSyllabus(request)
    }

    override suspend fun deleteSyllabus(ID: String): CommonResponse {
        return userService.deleteSyllabus(ID)
    }

    override suspend fun getVehicleNumber(): NetworkVehicleNumber {
        return userService.getVehicleNumber()
    }

    override suspend fun busLocation(vehicleNumber: String): NetworkBusLocation {
        return userService.busLocation(vehicleNumber)
    }

    override suspend fun postAssignmentRemark(request: List<AssignmentRemarkPost>): CommonResponse {
        return userService.postAssignmentRemark(request)
    }

    override suspend fun uploadStudentPhoto(request: StudentPhotoUploadModel): CommonResponse {
        return userService.uploadStudentPhoto(request)
    }

    override suspend fun academicYears(): NetworkAcademicYear {
        return userService.academicYears()
    }

    override suspend fun wingsList(): NetworkWingReport {
        return userService.wingsList()
    }

    override suspend fun feeCollection(
        feeTypeID: Int,
        fromDate: String,
        tillDate: String
    ): Flow<Result<FeeCollection>> {
        return flow {
            try {
                val response = userService.feeCollection(feeTypeID, fromDate, tillDate)
                emit(Result.success(response))
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override suspend fun surveyList(pg: Int, isReport: Boolean): SurveyListResponse {
        return userService.surveyList(pg, isReport)
    }

    override suspend fun surveyQuestions(id: String): SurveyQuestionsResponse {
        return userService.surveyQuestions(id)
    }

    override suspend fun submitSurveyQuestions(model: SurveyQuestionsSubmitRequest): CommonResponse {
        return userService.submitSurveyQuestions(model)
    }


    override fun getFormData(): Flow<Result<List<Form>>> {
        return flow {
            try {
                val response =
                    userService.getFormData("https://fomapi.franciscanecare.com/api/Master/getpageforsetting/${userDataStore.getSchoolData()?.schoolCode}/3")
                if (response.status == true) {
                    emit(Result.success(response.data ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(UNKNOWN_ERROR_MESSAGE)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getFormDataPurpose(): Flow<Result<List<Purpose>>> {
        return flow {
            try {
                val response =
                    userService.getFormDataPurpose("https://fomapi.franciscanecare.com/api/Master/getpurposes/${userDataStore.getSchoolData()?.schoolCode}")
                if (response.status == true) {
                    emit(Result.success(response.data ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(UNKNOWN_ERROR_MESSAGE)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getFormDataDepartment(): Flow<Result<List<Department>>> {
        return flow {
            try {
                val response =
                    userService.getFormDataDepartments("https://fomapi.franciscanecare.com/api/Master/getdepartments/${userDataStore.getSchoolData()?.schoolCode}")
                if (response.status == true) {
                    emit(Result.success(response.data ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(UNKNOWN_ERROR_MESSAGE)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getFormDataDesignationWithDepartment(departmentId: String): Flow<Result<List<Designation>>> {
        return flow {
            try {
                val response =
                    userService.getFormDataDesignationWithDepartment("https://fomapi.franciscanecare.com/api/Master/getdesignationWithDepartment/${userDataStore.getSchoolData()?.schoolCode}/$departmentId")
                if (response.status == true) {
                    emit(Result.success(response.data ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(UNKNOWN_ERROR_MESSAGE)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override suspend fun todayModeWiseCollection(date: String): Flow<Result<ModeWiseCollection>> {
        return flow {
            try {
                val response = userService.modeWiseCollection(date)
                emit(Result.success(response))
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getFormDataEmployee(
        departmentId: String,
        designation: String
    ): Flow<Result<List<Employee>>> {
        return flow {
            try {
                val response =
                    userService.getFormDataEmployee("https://fomapi.franciscanecare.com/api/Master/getemployees/${userDataStore.getSchoolData()?.schoolCode}/$departmentId/$designation")
                if (response.status == true) {
                    emit(Result.success(response.data ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(UNKNOWN_ERROR_MESSAGE)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getVisitorDetails(): Flow<Result<VisitorDetails>> {
        return flow {
            try {
                val response =
                    userService.getVisitorDetails("https://fomapi.franciscanecare.com/api/Appointment/getuserdetailsfrommobile/${userDataStore.getSchoolData()?.schoolCode}/${userDataStore.getUser()?.mobileNumber}")
                if (response.status == true) {
                    emit(Result.success(response.data!!))
                } else {
                    emit(Result.failure(IllegalArgumentException(UNKNOWN_ERROR_MESSAGE)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    @SuppressLint("NewApi")
    override fun submitForm(formData: Map<String, String>): Flow<Result<String>> {
        return flow {
            try {
                val builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)

                formData.forEach { (key, value) ->
                    builder.addFormDataPart(key, value)
                }

                val response = userService.submitForm(
                    url = "https://fomapi.franciscanecare.com/api/Appointment/saveappointmentmob/${userDataStore.getSchoolData()?.schoolCode}",
                    requestBody = builder.build()
                )
                if (response.status == true) {
                    emit(
                        Result.success(
                            response.data?.message ?: response.message
                            ?: "We have successfully updated your appointment to the school for review.Kindly check your message or email for current status of the appointment and confirmation code."
                        )
                    )
                } else {
                    emit(Result.failure(IllegalArgumentException(UNKNOWN_ERROR_MESSAGE)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    companion object {
        private const val USER_PROFILE_KEY = "user_profile"
        private const val USER_DASHBOARD_KEY = "user_dashboard"
        private const val USER_UNDERTAKING_KEY = "user_undertaking"
    }
}