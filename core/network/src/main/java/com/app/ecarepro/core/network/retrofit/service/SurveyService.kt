package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.survey.NetworkSurveyListResponse
import com.app.ecarepro.core.network.model.survey.NetworkSurveyPostAnswerRequest
import com.app.ecarepro.core.network.model.survey.NetworkSurveyQuestionsResponse
import kotlinx.serialization.InternalSerializationApi
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@OptIn(InternalSerializationApi::class)
interface SurveyService {

    @GET("Survey/List")
    suspend fun getSurveyList(): NetworkSurveyListResponse

    @GET("Survey/Questions")
    suspend fun getSurveyQuestions(@Query("ID") surveyId: String): NetworkSurveyQuestionsResponse

    @GET("Survey/Result")
    suspend fun getSurveyResult(@Query("ID") surveyId: String): NetworkSurveyQuestionsResponse

    @POST("Survey/PostAnswer")
    suspend fun postSurveyAnswer(@Body request: NetworkSurveyPostAnswerRequest): CommonNetworkResponse
}
