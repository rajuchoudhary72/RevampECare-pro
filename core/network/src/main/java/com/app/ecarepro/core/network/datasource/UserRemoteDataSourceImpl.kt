package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.UserRemoteDataSource
import com.app.ecarepro.core.network.model.questionnaire.NetworkAddQuestionRequest
import com.app.ecarepro.core.network.model.questionnaire.NetworkAddQuestionResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkAnswerListResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerRequest
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkQuestionnaireResponse
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.retrofit.service.UserService
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

@OptIn(InternalSerializationApi::class)
class UserRemoteDataSourceImpl @Inject constructor(
    private val userService: UserService,
) : UserRemoteDataSource {

    override suspend fun login(loginRequest: NetworkLoginRequest): NetworkLoginResponse {
        return userService
            .login(loginRequest)
            .unwrapPayload { this }
    }

    override suspend fun getQuestionnaireList(
        pg: Int,
        myQuestions: Boolean
    ): NetworkQuestionnaireResponse {
        return userService.getQuestionnaireList(pg, myQuestions)
    }

    override suspend fun getAnswerList(qid: Int): NetworkAnswerListResponse {
        return userService.getAnswerList(qid)
    }

    override suspend fun postAnswer(request: NetworkPostAnswerRequest): NetworkPostAnswerResponse {
        return userService.postAnswer(request)
    }

    override suspend fun addQuestion(request: NetworkAddQuestionRequest): NetworkAddQuestionResponse {
        return userService.addQuestion(request)
    }

}