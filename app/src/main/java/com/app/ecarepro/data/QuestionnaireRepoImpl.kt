package com.app.ecarepro.data

import com.app.ecarepro.data.network.model.NetworkQuestionnaire
import com.app.ecarepro.data.repository.QuestionnaireRepo
import javax.inject.Inject

class QuestionnaireRepoImpl @Inject constructor(
    private val questionnaireRepo: QuestionnaireRepo
) {

    suspend fun getQuestionnaireList( pg: Int, myque: Boolean ): NetworkQuestionnaire{
        return questionnaireRepo.getQuestionnaireList(pg, myque)
    }

}