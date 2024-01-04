package com.app.ecarepro.data

import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.database.model.asExternalModel
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.GetCredentialsRequest
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkUser
import com.app.ecarepro.data.network.model.UserLoginRequestDto
import com.app.ecarepro.data.network.model.VerifyUserDto
import com.app.ecarepro.data.network.model.asEntity
import com.app.ecarepro.data.network.service.UserService
import com.app.ecarepro.data.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDatabase: UserDatabase,
    private val userService: UserService,
    private val userDataStore: UserDataStore
) : UserRepository {
    override suspend fun insertUser(user: NetworkUser) {
        userDatabase.insertUser(user = user.asEntity())
        userDataStore.saveUser(user.asEntity().asExternalModel())
    }

    override suspend fun verifyUser(schoolCode: String, username: String): VerifyUserDto {
        return userService.verifyUser(schoolCode, username)
    }

    override suspend fun getCredentials(
        schoolCode: String,
        userType: Int,
        rcvOn: String,
        mobile: String?,
        email: String?
    ): VerifyUserDto {
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
            userDataStore.saveAuthToken(it.authToken ?: "")
            userDataStore.setAsUserAuthenticated(it.authenticated ?: false)
        }
    }

}