package com.app.ecarepro.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.app.ecarepro.data.database.model.SchoolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Insert
    suspend fun insertSchool(user: SchoolEntity)

    @Query("SELECT * FROM schools WHERE schoolCode = :schoolCode LIMIT 1")
    suspend fun getSchool(schoolCode: String): SchoolEntity

    @Query("SELECT * FROM schools WHERE schoolCode = :schoolCode LIMIT 1")
    suspend fun getSchoolData(schoolCode: String): SchoolEntity?

    @Query("SELECT * FROM schools WHERE schoolCode = :schoolCode LIMIT 1")
    fun getSchoolFlow(schoolCode: String): Flow<SchoolEntity>

    @Query("SELECT * FROM schools")
    fun getSchoolsFlow(): Flow<List<SchoolEntity>>

    @Delete
    fun deleteSchool(userEntity: SchoolEntity)
    @Query("DELETE FROM schools")
    suspend fun nukeTable()
}