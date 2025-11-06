package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.data.mapper.asEntity
import com.app.ecarepro.core.data.mapper.toDomainModel
import com.app.ecarepro.core.data.mapper.toLoginResult
import com.app.ecarepro.core.database.dao.UserDao
import com.app.ecarepro.core.database.model.UserEntity
import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.AppConfig
import com.app.ecarepro.core.domain.model.GetCredential
import com.app.ecarepro.core.domain.model.HomeScreenType
import com.app.ecarepro.core.domain.model.LoginResult
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.domain.model.Ward
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.network.UserRemoteDataSource
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
        val entity = userDetails.asEntity(schoolCode)
        val activeEntity = entity.copy(isActive = true)
        userDao.setActiveUser(activeEntity)
        return activeEntity
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

}