package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.GetCredential
import com.app.ecarepro.core.domain.model.HomeScreenType
import com.app.ecarepro.core.domain.model.LoginResult
import com.app.ecarepro.core.domain.model.AddQuestionRequest
import com.app.ecarepro.core.domain.model.AddQuestionResponse
import com.app.ecarepro.core.domain.model.AnswerListResponse
import com.app.ecarepro.core.domain.model.PostAnswerResponse
import com.app.ecarepro.core.domain.model.QuestionnaireResponse
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.domain.model.Ward
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun login(
        userName: String,
        password: String,
        schoolCode: String,
        location: String,
    ): Flow<Result<LoginResult>>

    fun resendOtp(schoolCode: String, otpAuthKey: String): Flow<Result<LoginResult>>

    fun validateOtp(schoolCode: String, otpAuthKey: String, otp: String): Flow<Result<LoginResult>>

    suspend fun getHomeScreenType(): HomeScreenType

    suspend fun saveHomeScreenType(homeScreenType: HomeScreenType)
    fun getQuestions(
        page: Int,
        myQuestions: Boolean
    ): Flow<Result<QuestionnaireResponse>>

    fun getAnswerList(qid: Int): Flow<Result<AnswerListResponse>>

    fun postAnswer(qid: Int, answer: String): Flow<Result<PostAnswerResponse>>

    fun addQuestion(request: AddQuestionRequest): Flow<Result<AddQuestionResponse>>
    suspend fun getActiveUser(): User?
    suspend fun getActiveUserAuthToken(): String?
    suspend fun getCredential(getCredential: GetCredential): Flow<Result<Pair<String, List<Ward>>>>
    suspend fun getUsernameByUID(
        schoolCode: String,
        userID: Int,
        userType: Int,
        receivedOn: String,
    ): Flow<Result<String>>
}