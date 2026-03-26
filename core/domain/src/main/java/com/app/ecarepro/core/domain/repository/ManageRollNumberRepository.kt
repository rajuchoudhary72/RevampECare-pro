package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.RollNumberStudent
import kotlinx.coroutines.flow.Flow

interface ManageRollNumberRepository {
    fun getClassTeacherOf(): Flow<Result<List<Class>>>
    fun getStudentsForRollNumber(classId: String, orderBy: Int = 0): Flow<Result<List<RollNumberStudent>>>
    fun assignRollNumbers(assignments: List<RollNumberAssignment>): Flow<Result<String>>
}

data class RollNumberAssignment(
    val stID: Int,
    val houseID: Int,
    val rollNumber: String,
)
