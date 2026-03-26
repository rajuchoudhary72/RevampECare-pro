package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.survey.SurveyAnswerRequest
import com.app.ecarepro.core.domain.model.survey.SurveyItem
import com.app.ecarepro.core.domain.model.survey.SurveyQuestion
import com.app.ecarepro.core.domain.repository.SurveyRepository
import com.app.ecarepro.core.network.SurveyRemoteDataSource
import com.app.ecarepro.core.network.model.survey.NetworkSurveyOptionPayload
import com.app.ecarepro.core.network.model.survey.NetworkSurveyPostAnswerRequest
import com.app.ecarepro.core.network.model.survey.NetworkSurveyQuestionPayload
import com.app.ecarepro.core.network.model.survey.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SurveyRepositoryImpl @Inject constructor(
    private val dataSource: SurveyRemoteDataSource,
) : SurveyRepository {

    override fun getSurveyList(): Flow<Result<List<SurveyItem>>> = asResultFlow {
        dataSource.getSurveyList().map { it.toDomainModel() }
    }

    override fun getSurveyQuestions(surveyId: String): Flow<Result<List<SurveyQuestion>>> = asResultFlow {
        val response = dataSource.getSurveyQuestions(surveyId)
        response.questions?.map { it.toDomainModel() }.orEmpty()
    }

    override fun getSurveyResult(surveyId: String): Flow<Result<List<SurveyQuestion>>> = asResultFlow {
        val response = dataSource.getSurveyResult(surveyId)
        response.questions?.map { it.toDomainModel() }.orEmpty()
    }

    override fun postSurveyAnswer(request: SurveyAnswerRequest): Flow<Result<String>> = asResultFlow {
        val networkRequest = NetworkSurveyPostAnswerRequest(
            id = request.id,
            questions = request.questions.map { q ->
                NetworkSurveyQuestionPayload(
                    queID = q.queID,
                    question = q.question,
                    isMultiSelect = q.isMultiSelect,
                    isAnsMandatory = q.isAnsMandatory,
                    response = q.response,
                    answer = q.answer,
                    options = q.options.map { opt ->
                        NetworkSurveyOptionPayload(
                            optID = opt.optID,
                            option = opt.option,
                            isSelected = opt.isSelected,
                            response = opt.response,
                        )
                    },
                )
            },
        )
        val response = dataSource.postSurveyAnswer(networkRequest)
        if (response.status == "ok") {
            response.message
        } else {
            throw Exception(response.message)
        }
    }
}
