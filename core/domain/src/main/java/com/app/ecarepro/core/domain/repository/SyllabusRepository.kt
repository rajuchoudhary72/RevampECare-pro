package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.SaveSyllabus
import com.app.ecarepro.core.domain.model.Section
import com.app.ecarepro.core.domain.model.Subject
import com.app.ecarepro.core.domain.model.Syllabus
import kotlinx.coroutines.flow.Flow

interface SyllabusRepository {
    fun getSyllabus(): Flow<Result<List<Syllabus>>>
    fun deleteSyllabus(syllabusId: String): Flow<Result<Boolean>>

    fun getClasses(): Flow<Result<List<Class>>>

    fun getSections(classStd: String): Flow<Result<List<Section>>>

    fun getSubjects(classStd: String): Flow<Result<List<Subject>>>

    fun saveSyllabus(syllabus: SaveSyllabus): Flow<Result<String>>
}


