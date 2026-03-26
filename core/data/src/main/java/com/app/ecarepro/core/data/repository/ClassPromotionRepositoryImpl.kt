package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.ClassPromotionData
import com.app.ecarepro.core.domain.model.StudentPromotion
import com.app.ecarepro.core.domain.repository.ClassPromotionRepository
import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.StaffRemoteDataSource
import com.app.ecarepro.core.network.model.admin.NetworkSaveClassPromotionRequest
import com.app.ecarepro.core.network.model.admin.NetworkStudentPromotedClass
import com.app.ecarepro.core.network.model.admin.toDomainModel as toClassPromotionDomain
import com.app.ecarepro.core.network.model.staff.toDomainModel as toClassDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ClassPromotionRepositoryImpl @Inject constructor(
    private val staffRemoteDataSource: StaffRemoteDataSource,
    private val adminRemoteDataSource: AdminRemoteDataSource,
) : ClassPromotionRepository {

    override fun getClassTeacherOf(): Flow<Result<List<Class>>> = asResultFlow {
        staffRemoteDataSource.getClassTeacherOf().map { it.toClassDomain() }
    }

    override fun getClassPromotion(classId: String): Flow<Result<ClassPromotionData>> = asResultFlow {
        adminRemoteDataSource.getClassPromotion(classId).toClassPromotionDomain()
    }

    override fun saveClassPromotion(
        yrID: Int,
        nYrID: Int,
        studentPromotions: List<StudentPromotion>,
    ): Flow<Result<String>> = asResultFlow {
        adminRemoteDataSource.saveClassPromotion(
            NetworkSaveClassPromotionRequest(
                yrID = yrID,
                nYrID = nYrID,
                studentPromotedClasses = studentPromotions.map {
                    NetworkStudentPromotedClass(
                        stID = it.stID,
                        newClassID = it.newClassID,
                        newSectionID = it.newSectionID,
                    )
                },
            )
        )
    }
}
