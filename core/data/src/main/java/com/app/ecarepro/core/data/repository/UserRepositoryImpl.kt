package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.data.mapper.asEntity
import com.app.ecarepro.core.data.mapper.toDomainModel
import com.app.ecarepro.core.database.dao.UserDao
import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.AppConfig
import com.app.ecarepro.core.domain.model.HomeScreenType
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.network.UserRemoteDataSource
import com.app.ecarepro.core.network.model.user.NetworkDeviceInfo
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.let

class UserRepositoryImpl @Inject constructor(
    private val appConfig: AppConfig,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao,
) : UserRepository {
    override fun login(
        userName: String,
        password: String,
        schoolCode: String,
    ): Flow<Result<User>> {
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
                        locationCity = appConfig.location,
                        osVersion = appConfig.osVersion
                    )
                )
            ).let { response ->
                val entity = response.userDTL!!.asEntity(schoolCode)
                userDao.clearActiveUser()
                userDao.insertOrUpdateUser(entity.copy(isActive = true))
                entity.toDomainModel()
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
        userDao.getActiveUser()?.let { activeUser ->
            userDao.updateUser(activeUser.copy(homeScreenType = homeScreenType.id))
        }
    }

    override suspend fun getActiveUser(): User? {
        return userDao.getActiveUser()?.toDomainModel()
    }
}