package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.StaffRemoteDataSource
import com.app.ecarepro.core.network.model.staff.NetworkClass
import com.app.ecarepro.core.network.model.staff.NetworkSection
import com.app.ecarepro.core.network.model.staff.NetworkSubject
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.StaffService
import javax.inject.Inject

internal class StaffRemoteDataSourceImpl @Inject constructor(
    private val staffService: StaffService,
) : StaffRemoteDataSource {

    override suspend fun getClasses(): List<NetworkClass> {
        return staffService.getClasses().unwrapPayload { myClasses }
    }

    override suspend fun getMyClasses(): List<NetworkClass> {
        return staffService.getClasses(null, null).unwrapPayload { myClasses }
    }

    override suspend fun getSections(classStd: String): List<NetworkSection> {
        return staffService.getSections(classStd).unwrapPayload { sections }
    }

    override suspend fun getSubjects(classStd: String): List<NetworkSubject> {
        return staffService.getSubjects(classStd).unwrapPayload { mySubjects }
    }

    override suspend fun getMySubjects(): List<NetworkSubject> {
        return staffService.getMySubjects().unwrapPayload { mySubjects }
    }

}