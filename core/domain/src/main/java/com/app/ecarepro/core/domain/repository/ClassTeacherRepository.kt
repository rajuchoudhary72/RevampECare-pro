package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.ClassTeacher
import kotlinx.coroutines.flow.Flow

interface ClassTeacherRepository {
    /** Fetches the class teacher list from GET Report/Classteacher. */
    fun getClassTeachers(): Flow<Result<List<ClassTeacher>>>
}
