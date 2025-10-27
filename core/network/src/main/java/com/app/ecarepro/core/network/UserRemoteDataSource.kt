package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.questionnaire.NetworkAnswerListResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerRequest
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkQuestionnaireResponse
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
interface UserRemoteDataSource {
    suspend fun login(loginRequest: NetworkLoginRequest): NetworkLoginResponse

    suspend fun getQuestionnaireList(pg: Int, myQuestions: Boolean): NetworkQuestionnaireResponse

    suspend fun getAnswerList(qid: Int): NetworkAnswerListResponse

    suspend fun postAnswer(request: NetworkPostAnswerRequest): NetworkPostAnswerResponse

}