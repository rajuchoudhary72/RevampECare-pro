package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.RollNumberStudent
import com.app.ecarepro.core.domain.repository.ManageRollNumberRepository
import com.app.ecarepro.core.domain.repository.RollNumberAssignment
import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.StaffRemoteDataSource
import com.app.ecarepro.core.network.model.admin.NetworkAssignRollNumberItem
import com.app.ecarepro.core.network.model.admin.toDomainModel as toRollNumberDomain
import com.app.ecarepro.core.network.model.staff.toDomainModel as toClassDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ManageRollNumberRepositoryImpl @Inject constructor(
    private val staffRemoteDataSource: StaffRemoteDataSource,
    private val adminRemoteDataSource: AdminRemoteDataSource,
) : ManageRollNumberRepository {

    override fun getClassTeacherOf(): Flow<Result<List<Class>>> = asResultFlow {
        staffRemoteDataSource.getClassTeacherOf().map { it.toClassDomain() }
    }

    override fun getStudentsForRollNumber(classId: String, orderBy: Int): Flow<Result<List<RollNumberStudent>>> = asResultFlow {
        adminRemoteDataSource.getStudentsForRollNumber(classId, orderBy).map { it.toRollNumberDomain() }
    }

    override fun assignRollNumbers(assignments: List<RollNumberAssignment>): Flow<Result<String>> = asResultFlow {
        adminRemoteDataSource.assignRollNumbers(
            assignments.map {
                NetworkAssignRollNumberItem(
                    stID = it.stID,
                    houseID = it.houseID,
                    rollNumber = it.rollNumber,
                )
            }
        )
    }
}
