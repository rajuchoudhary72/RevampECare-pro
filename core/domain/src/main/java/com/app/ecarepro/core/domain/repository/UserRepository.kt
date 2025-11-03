package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.AddQuestionRequest
import com.app.ecarepro.core.domain.model.AddQuestionResponse
import com.app.ecarepro.core.domain.model.AnswerListResponse
import com.app.ecarepro.core.domain.model.PostAnswerResponse
import com.app.ecarepro.core.domain.model.QuestionnaireResponse
import com.app.ecarepro.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun login(
        userName: String,
        password: String,
        schoolCode: String,
    ): Flow<Result<User>>


    fun getQuestions(
        page: Int,
        myQuestions: Boolean
    ): Flow<Result<QuestionnaireResponse>>

    fun getAnswerList(qid: Int): Flow<Result<AnswerListResponse>>

    fun postAnswer(qid: Int, answer: String): Flow<Result<PostAnswerResponse>>

    fun addQuestion(request: AddQuestionRequest): Flow<Result<AddQuestionResponse>>
}