package com.app.ecarepro.data.database.databases

import com.app.ecarepro.data.database.model.SchoolEntity
import kotlinx.coroutines.flow.Flow

interface SchoolDatabase {
    suspend fun insertSchool(user: SchoolEntity)
    suspend fun getSchool(schoolCode: String): SchoolEntity
    suspend fun getSchoolData(schoolCode: String): SchoolEntity?
    fun getSchoolFlow(schoolCode: String): Flow<SchoolEntity>
    fun getSchoolsFlow(): Flow<List<SchoolEntity>>
    suspend fun deleteSchool(schoolEntity: SchoolEntity)
}