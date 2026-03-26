package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.ClassPromotionData
import com.app.ecarepro.core.domain.model.StudentPromotion
import kotlinx.coroutines.flow.Flow

interface ClassPromotionRepository {
    fun getClassTeacherOf(): Flow<Result<List<Class>>>
    fun getClassPromotion(classId: String): Flow<Result<ClassPromotionData>>
    fun saveClassPromotion(
        yrID: Int,
        nYrID: Int,
        studentPromotions: List<StudentPromotion>,
    ): Flow<Result<String>>
}
