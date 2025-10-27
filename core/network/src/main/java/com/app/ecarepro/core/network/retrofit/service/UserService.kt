package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.questionnaire.NetworkAnswerListResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerRequest
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkQuestionnaireResponse
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import kotlinx.serialization.InternalSerializationApi
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@OptIn(InternalSerializationApi::class)
interface UserService {


    @POST("User/TwoFactorLogin")
    suspend fun login(
        @Body loginRequest: NetworkLoginRequest,
    ): NetworkLoginResponse

    @GET("Questionnaire/List")
    suspend fun getQuestionnaireList(
        @Query("pg") pg: Int,
        @Query("myque") myQuestions: Boolean,
    ): NetworkQuestionnaireResponse

    @GET("Questionnaire/AnswerList")
    suspend fun getAnswerList(
        @Query("QID") qid: Int
    ): NetworkAnswerListResponse

    @POST("Questionnaire/PostAnswer")
    suspend fun postAnswer(
        @Body request: NetworkPostAnswerRequest
    ): NetworkPostAnswerResponse

}