package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.HouseStudentData
import com.app.ecarepro.core.domain.repository.HouseAssignment
import com.app.ecarepro.core.domain.repository.UpdateHouseRepository
import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.StaffRemoteDataSource
import com.app.ecarepro.core.network.model.admin.NetworkAssignHouseItem
import com.app.ecarepro.core.network.model.admin.toDomainModel as toHouseDomain
import com.app.ecarepro.core.network.model.staff.toDomainModel as toClassDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UpdateHouseRepositoryImpl @Inject constructor(
    private val staffRemoteDataSource: StaffRemoteDataSource,
    private val adminRemoteDataSource: AdminRemoteDataSource,
) : UpdateHouseRepository {

    override fun getClassTeacherOf(): Flow<Result<List<Class>>> = asResultFlow {
        staffRemoteDataSource.getClassTeacherOf().map { it.toClassDomain() }
    }

    override fun getStudentsForHouse(classId: String, orderBy: Int): Flow<Result<HouseStudentData>> = asResultFlow {
        val response = adminRemoteDataSource.getStudentsForHouse(classId, orderBy)
        HouseStudentData(
            houses = response.houses?.map { it.toHouseDomain() } ?: emptyList(),
            students = response.students?.map { it.toHouseDomain() } ?: emptyList(),
        )
    }

    override fun assignHouses(assignments: List<HouseAssignment>): Flow<Result<String>> = asResultFlow {
        adminRemoteDataSource.assignHouses(
            assignments.map {
                NetworkAssignHouseItem(
                    stID = it.stID,
                    houseID = it.houseID,
                    rollNumber = it.rollNumber,
                )
            }
        )
    }
}
