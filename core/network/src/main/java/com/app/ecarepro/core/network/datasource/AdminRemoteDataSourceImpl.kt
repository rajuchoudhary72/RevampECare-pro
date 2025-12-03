package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.model.admin.NetworkSaveSyllabus
import com.app.ecarepro.core.network.model.admin.NetworkSyllabus
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.AdminService
import javax.inject.Inject

internal class AdminRemoteDataSourceImpl @Inject constructor(
    private val adminService: AdminService,
) : AdminRemoteDataSource {
    override suspend fun getSyllabus(): List<NetworkSyllabus> {
        return adminService.getSyllabus().unwrapPayload { syllabuses }
    }

    override suspend fun deleteSyllabus(syllabusId: String): Boolean {
        return adminService.deleteSyllabus(syllabusId).unwrapPayload { true }
    }

    override suspend fun saveSyllabus(syllabus: NetworkSaveSyllabus): String {
        return adminService.saveSyllabus(syllabus).unwrapPayload { message }
    }

}