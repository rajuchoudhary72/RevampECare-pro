package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.data.mapper.asEntity
import com.app.ecarepro.core.data.mapper.toDomainModel
import com.app.ecarepro.core.data.mapper.toLoginResult
import com.app.ecarepro.core.data.mapper.toNetworkModel
import com.app.ecarepro.core.database.dao.SchoolDao
import com.app.ecarepro.core.database.dao.UserDao
import com.app.ecarepro.core.database.model.UserEntity
import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.AddQuestionRequest
import com.app.ecarepro.core.domain.model.AddQuestionResponse
import com.app.ecarepro.core.domain.model.AnswerListResponse
import com.app.ecarepro.core.domain.model.AppConfig
import com.app.ecarepro.core.domain.model.GetCredential
import com.app.ecarepro.core.domain.model.HomeScreenType
import com.app.ecarepro.core.domain.model.LeaveActionRequest
import com.app.ecarepro.core.domain.model.LeaveActionResponse
import com.app.ecarepro.core.domain.model.LeaveReportResponse
import com.app.ecarepro.core.domain.model.LoginResult
import com.app.ecarepro.core.domain.model.SavedAccountItem
import com.app.ecarepro.core.domain.model.PostAnswerResponse
import com.app.ecarepro.core.domain.model.QuestionnaireResponse
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.domain.model.Ward
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.network.UserRemoteDataSource
import com.app.ecarepro.core.network.model.leave.toDomainModel
import com.app.ecarepro.core.network.model.leave.toNetworkModel
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerRequest
import com.app.ecarepro.core.network.model.user.NetworkDeviceInfo
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.model.user.NetworkResendOtpRequest
import com.app.ecarepro.core.network.model.user.NetworkValidateOtpRequest
import com.app.ecarepro.core.network.model.user.UserDetails
import com.app.ecarepro.core.network.model.user.toNetworkGetCredentialRequest
import com.app.ecarepro.core.network.model.user.toWard
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val appConfig: AppConfig,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao,
    private val schoolDao: SchoolDao,
) : UserRepository {


    override fun login(
        userName: String,
        password: String,
        schoolCode: String,
        location: String,
    ): Flow<Result<LoginResult>> {
        return asResultFlow {
            userRemoteDataSource.login(
                NetworkLoginRequest(
                    userName = userName,
                    password = password,
                    schoolCode = schoolCode,
                    deviceInfo = NetworkDeviceInfo(
                        appVersion = appConfig.appVersion,
                        deviceType = appConfig.deviceType.id,
                        model = appConfig.deviceModel,
                        ipAddress = appConfig.deviceId,
                        locationCity = location,
                        osVersion = appConfig.osVersion
                    )
                )
            ).let { response: NetworkLoginResponse ->
                processLoginResponse(response, schoolCode)
            }
        }
    }

    private suspend fun replaceActiveUser(
        userDetails: UserDetails,
        schoolCode: String,
    ): UserEntity {
        val newEntity = userDetails.asEntity(schoolCode)
        // If an account with same (userID, userType) already exists, reuse its local id to avoid duplicates
        val existing = if (newEntity.userID != null && newEntity.userType != null) {
            userDao.getUserByUserIdAndType(newEntity.userID!!, newEntity.userType!!)
        } else null
        val entityToSave = if (existing != null) {
            newEntity.copy(id = existing.id, isActive = true)
        } else {
            newEntity.copy(isActive = true)
        }
        userDao.setActiveUser(entityToSave)
        return entityToSave
    }

    private suspend fun processLoginResponse(
        response: NetworkLoginResponse,
        schoolCode: String,
    ): LoginResult {
        val loginResult = response.toLoginResult()
        return response.userDTL?.let { userDetails ->
            val entity = replaceActiveUser(userDetails, schoolCode)
            loginResult.copy(userDetail = entity.toDomainModel())
        } ?: loginResult
    }

    override fun resendOtp(
        schoolCode: String,
        otpAuthKey: String,
    ): Flow<Result<LoginResult>> {
        return asResultFlow {
            userRemoteDataSource.resendOtp(
                NetworkResendOtpRequest(
                    schoolCode = schoolCode,
                    otpAuthKey = otpAuthKey
                )
            ).toLoginResult()
        }
    }

    override fun validateOtp(
        schoolCode: String,
        otpAuthKey: String,
        otp: String,
    ): Flow<Result<LoginResult>> {
        return asResultFlow {
            userRemoteDataSource.validateOtp(
                NetworkValidateOtpRequest(
                    schoolCode = schoolCode,
                    otpAuthKey = otpAuthKey,
                    otp = otp
                )
            ).let { response: NetworkLoginResponse ->
                processLoginResponse(response, schoolCode)
            }
        }
    }

    override suspend fun getHomeScreenType(): HomeScreenType {
        return userDao.getActiveUser()?.homeScreenType?.let {
            HomeScreenType.getHomeScreenTypeById(
                it
            )
        } ?: HomeScreenType.DASHBOARD
    }

    override suspend fun saveHomeScreenType(homeScreenType: HomeScreenType) {
        userDao.updateActiveUserHomeScreenType(homeScreenType.id)
    }

    override suspend fun getActiveUser(): User? {
        return userDao.getActiveUser()?.toDomainModel()
    }

    override suspend fun getActiveUserAuthToken(): String? {
        return userDao.getActiveUserToken()
    }

    override suspend fun getCredential(getCredential: GetCredential): Flow<Result<Pair<String, List<Ward>>>> {
        return asResultFlow {
            val response = userRemoteDataSource
                .getCredentials(getCredential.toNetworkGetCredentialRequest())
            Pair(response.message, response.wards?.map { it.toWard() } ?: emptyList())
        }
    }

    override suspend fun getUsernameByUID(
        schoolCode: String,
        userID: Int,
        userType: Int,
        receivedOn: String,
    ): Flow<Result<String>> {
        return asResultFlow {
            userRemoteDataSource
                .getUsernameByUID(
                    schoolCode = schoolCode,
                    userID = userID,
                    userType = userType,
                    receivedOn = receivedOn
                )
                .message
        }
    }

    override fun getQuestions(
        page: Int,
        myQuestions: Boolean
    ): Flow<Result<QuestionnaireResponse>> {
        return asResultFlow {
            userRemoteDataSource.getQuestionnaireList(page, myQuestions).toDomainModel()
        }
    }

    override fun getAnswerList(qid: Int): Flow<Result<AnswerListResponse>> {
        return asResultFlow {
            userRemoteDataSource.getAnswerList(qid).toDomainModel()
        }
    }

    override fun postAnswer(qid: Int, answer: String): Flow<Result<PostAnswerResponse>> {
        return asResultFlow {
            userRemoteDataSource.postAnswer(
                NetworkPostAnswerRequest(
                    qid = qid,
                    answer = answer
                )
            ).toDomainModel()
        }
    }

    override fun addQuestion(request: AddQuestionRequest): Flow<Result<AddQuestionResponse>> {
        return asResultFlow {
            userRemoteDataSource.addQuestion(request.toNetworkModel()).toDomainModel()
        }
    }

    override fun getLeaveReport(
        status: Int,
        order: Int,
        applType: Int,
        page: Int,
        showAttendance: Boolean,
        duration: Int
    ): Flow<Result<LeaveReportResponse>> {
        return asResultFlow {
            userRemoteDataSource.getLeaveReport(
                status = status,
                order = order,
                applType = applType,
                page = page,
                showAttendance = showAttendance,
                duration = duration
            ).toDomainModel()
        }
    }

    override fun leaveAction(request: LeaveActionRequest): Flow<Result<LeaveActionResponse>> {
        return asResultFlow {
            userRemoteDataSource.leaveAction(request.toNetworkModel()).toDomainModel()
        }
    }

    override suspend fun clearSession() {
        userDao.clearActiveUser()
    }

    override suspend fun logout() {
        val activeUser = userDao.getActiveUser()
        // Best-effort API call — proceed with local clear even if it fails
        try {
            userRemoteDataSource.logout(
                deviceID = appConfig.deviceId,
                sessionID = activeUser?.sessionID.orEmpty(),
            )
        } catch (_: Exception) {}
        // Wipe all local data
        userDao.deleteAll()
        schoolDao.deleteAll()
    }

    override suspend fun getAllSavedAccounts(): List<SavedAccountItem> {
        val users = userDao.getAllUsers()
        val schools = schoolDao.getAllSchools().associateBy { it.schoolCode }
        return users.map { user ->
            val school = schools[user.schoolCode]
            SavedAccountItem(
                localId = user.id,
                name = user.name.orEmpty(),
                roleName = user.roleName.orEmpty(),
                schoolCode = user.schoolCode,
                schoolName = school?.schoolName ?: user.schoolCode,
                schoolLogo = school?.logo,
                userPhoto = user.photoPath,
                isActive = user.isActive,
                userType = user.userType,
                className = user.className,
                stName = user.stName,
            )
        }
    }

    override suspend fun switchToAccount(localId: Int) {
        val user = userDao.getUserByLocalId(localId) ?: return
        userDao.setActiveUser(user.copy(isActive = true))
    }

    override suspend fun deleteAccount(localId: Int) {
        userDao.deleteUserByLocalId(localId)
    }
}
