package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.SurveyRemoteDataSource
import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.survey.NetworkSurveyItem
import com.app.ecarepro.core.network.model.survey.NetworkSurveyPostAnswerRequest
import com.app.ecarepro.core.network.model.survey.NetworkSurveyQuestionsResponse
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.SurveyService
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

@OptIn(InternalSerializationApi::class)
internal class SurveyRemoteDataSourceImpl @Inject constructor(
    private val service: SurveyService,
) : SurveyRemoteDataSource {

    override suspend fun getSurveyList(): List<NetworkSurveyItem> =
        service.getSurveyList().unwrapPayload { allSurvey.orEmpty() }

    override suspend fun getSurveyQuestions(surveyId: String): NetworkSurveyQuestionsResponse =
        service.getSurveyQuestions(surveyId).also {
            if (it.errorCode != 0) throw Exception(it.message)
        }

    override suspend fun getSurveyResult(surveyId: String): NetworkSurveyQuestionsResponse =
        service.getSurveyResult(surveyId).also {
            if (it.errorCode != 0) throw Exception(it.message)
        }

    override suspend fun postSurveyAnswer(request: NetworkSurveyPostAnswerRequest): CommonNetworkResponse =
        service.postSurveyAnswer(request)
}
