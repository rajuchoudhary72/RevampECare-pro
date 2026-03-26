package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.StaffProfile
import kotlinx.coroutines.flow.Flow

interface KnowYourTeacherRepository {
    /** Fetches the teacher list from GET Staff/List. */
    fun getTeacherList(): Flow<Result<List<StaffProfile>>>
}
