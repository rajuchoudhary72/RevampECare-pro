package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.survey.NetworkSurveyItem
import com.app.ecarepro.core.network.model.survey.NetworkSurveyPostAnswerRequest
import com.app.ecarepro.core.network.model.survey.NetworkSurveyQuestionsResponse
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
interface SurveyRemoteDataSource {
    suspend fun getSurveyList(): List<NetworkSurveyItem>
    suspend fun getSurveyQuestions(surveyId: String): NetworkSurveyQuestionsResponse
    suspend fun getSurveyResult(surveyId: String): NetworkSurveyQuestionsResponse
    suspend fun postSurveyAnswer(request: NetworkSurveyPostAnswerRequest): CommonNetworkResponse
}
