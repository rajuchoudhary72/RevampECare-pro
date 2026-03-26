package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.sms.NetworkSmsStudents

interface SmsRemoteDataSource {
    suspend fun getStudents(
        teacherId: String,
        classId: String,
        scholarType: String,
    ): NetworkSmsStudents
}