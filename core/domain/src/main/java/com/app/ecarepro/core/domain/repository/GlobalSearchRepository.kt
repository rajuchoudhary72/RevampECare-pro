package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.globalsearch.SearchStaff
import com.app.ecarepro.core.domain.model.globalsearch.SearchStudent
import kotlinx.coroutines.flow.Flow

interface GlobalSearchRepository {
    fun getStudents(): Flow<Result<List<SearchStudent>>>
    fun getStaff(): Flow<Result<List<SearchStaff>>>
}
