package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkQuestionnaire
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface QuestionnaireService {

    @Headers("Accept: application/json")
    @GET("Questionnaire/List")
    suspend fun getQuestionnaireList(
        @Query("pg") pg: Int,
        @Query("myque") myque: Boolean,
    ): NetworkQuestionnaire

}