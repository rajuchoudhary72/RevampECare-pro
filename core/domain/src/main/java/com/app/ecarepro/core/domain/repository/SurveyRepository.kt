package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.survey.SurveyAnswerRequest
import com.app.ecarepro.core.domain.model.survey.SurveyItem
import com.app.ecarepro.core.domain.model.survey.SurveyQuestion
import kotlinx.coroutines.flow.Flow

interface SurveyRepository {
    fun getSurveyList(): Flow<Result<List<SurveyItem>>>
    fun getSurveyQuestions(surveyId: String): Flow<Result<List<SurveyQuestion>>>
    fun getSurveyResult(surveyId: String): Flow<Result<List<SurveyQuestion>>>
    fun postSurveyAnswer(request: SurveyAnswerRequest): Flow<Result<String>>
}
