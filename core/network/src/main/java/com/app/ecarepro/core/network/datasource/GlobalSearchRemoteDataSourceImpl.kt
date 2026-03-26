package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.GlobalSearchRemoteDataSource
import com.app.ecarepro.core.network.model.globalsearch.NetworkSearchStaff
import com.app.ecarepro.core.network.model.globalsearch.NetworkSearchStudent
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.GlobalSearchService
import javax.inject.Inject

internal class GlobalSearchRemoteDataSourceImpl @Inject constructor(
    private val service: GlobalSearchService,
) : GlobalSearchRemoteDataSource {

    override suspend fun getStudents(): List<NetworkSearchStudent> =
        service.getStudentList().unwrapPayload { students.orEmpty() }

    override suspend fun getStaff(): List<NetworkSearchStaff> =
        service.getStaffList().unwrapPayload { staffs.orEmpty() }
}
