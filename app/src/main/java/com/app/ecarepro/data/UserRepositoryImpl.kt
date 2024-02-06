package com.app.ecarepro.data

import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.datastore.UserDataStore
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
import com.app.ecarepro.data.network.model.NetworkUser
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.NetworkWhoLike
import com.app.ecarepro.data.network.model.UserLoginRequestDto
import com.app.ecarepro.data.network.model.asEntity
import com.app.ecarepro.data.network.model.AddThoughtsPostData
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
            userDataStore.saveAuthToken(it.authToken ?: "")
            userDataStore.setAsUserAuthenticated(it.authenticated ?: false)
        }
    }

    override suspend fun getClassSyllabus(): NetworkClassSyllabus {
        return userService.getClassSyllabus()
    }

    override suspend fun getActivityCaledar(): NetworkActivityCalender {
        return  userService.getActivityCaledar()
    }

    override suspend fun getLibraryDTL(): NetworkLatestBook {
        return userService.getLibraryDTL()
    }

    override suspend fun getBookDTL(bookID: Int, id: Int): NetworkBookDetails {
        return userService.getBookDTL(bookID, id)
    }

    override suspend fun getLibrarySearch(query: String, pg: Int): NetworkBookDetails {
        return   userService.getLibrarySearch(query, pg)
    }

    override suspend fun getQuestionnaireList(pg: Int, myque: Boolean ): NetworkQuestionnaire {
        return userService.getQuestionnaireList(pg, myque)
    }

    override suspend fun getMyClass(subID: Int, iD: Int): NetworkMyClass {
        return  userService.myClass()
    }

    override suspend fun getPayslip(): NetworkPaySlip {
        return userService.getPayslip()
    }

    override suspend fun getThoughts(pg: Int, dir: Int, mythoughts: Boolean): NetworkThoughts {
        return  userService.getThoughts(pg, dir, mythoughts)
    }

    override suspend fun like(thID: Int, like: Boolean): CommonResponse {
        return userService.like(thID, like)
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

}