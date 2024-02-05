package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.NetworkQuestionnaire
import retrofit2.http.Query

interface QuestionnaireRepo {

    suspend fun getQuestionnaireList( pg: Int, myque: Boolean ): NetworkQuestionnaire

}