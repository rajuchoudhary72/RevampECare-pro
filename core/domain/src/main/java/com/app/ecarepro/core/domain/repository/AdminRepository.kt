package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.Syllabus
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getSyllabus(): Flow<Result<List<Syllabus>>>
    fun deleteSyllabus(syllabusId: String): Flow<Result<Boolean>>
}


