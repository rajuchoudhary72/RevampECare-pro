package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.HouseStudentData
import kotlinx.coroutines.flow.Flow

interface UpdateHouseRepository {
    fun getClassTeacherOf(): Flow<Result<List<Class>>>
    fun getStudentsForHouse(classId: String, orderBy: Int = 0): Flow<Result<HouseStudentData>>
    fun assignHouses(assignments: List<HouseAssignment>): Flow<Result<String>>
}

data class HouseAssignment(
    val stID: Int,
    val houseID: Int,
    val rollNumber: String,
)
