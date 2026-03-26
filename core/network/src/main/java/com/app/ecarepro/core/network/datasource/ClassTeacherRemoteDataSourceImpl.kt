package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.ClassTeacherRemoteDataSource
import com.app.ecarepro.core.network.model.classteacher.NetworkClassTeacher
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.ReportService
import javax.inject.Inject

internal class ClassTeacherRemoteDataSourceImpl @Inject constructor(
    private val service: ReportService,
) : ClassTeacherRemoteDataSource {

    override suspend fun getClassTeachers(): List<NetworkClassTeacher> =
        service.getClassTeachers().unwrapPayload { teachers ?: emptyList() }
}
