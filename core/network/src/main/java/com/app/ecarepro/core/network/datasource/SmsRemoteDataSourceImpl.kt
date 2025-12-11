package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.SmsRemoteDataSource
import com.app.ecarepro.core.network.model.sms.NetworkSmsStudents
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.SmsService
import javax.inject.Inject

internal class SmsRemoteDataSourceImpl @Inject constructor(
    private val smsService: SmsService,
) : SmsRemoteDataSource {

    override suspend fun getStudents(
        teacherId: String,
        classId: String,
        scholarType: String,
    ): NetworkSmsStudents {
        return smsService.getStudents(
            teacherId = teacherId,
            classId = classId,
            scholarType = scholarType
        )
            .unwrapPayload { this }

    }
}