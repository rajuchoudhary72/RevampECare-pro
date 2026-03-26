package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.QuestionPaperResponse
import kotlinx.coroutines.flow.Flow

interface QuestionPaperRepository {
    suspend fun getStaffClasses(): List<Class>
    suspend fun getQuestionPapers(classId: Int, yrId: Int): QuestionPaperResponse
}
