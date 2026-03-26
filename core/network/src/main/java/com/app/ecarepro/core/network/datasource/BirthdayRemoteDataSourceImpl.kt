package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.BirthdayRemoteDataSource
import com.app.ecarepro.core.network.model.birthday.NetworkUserBirthday
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.ReportService
import javax.inject.Inject

internal class BirthdayRemoteDataSourceImpl @Inject constructor(
    private val service: ReportService,
) : BirthdayRemoteDataSource {
    override suspend fun getBirthdays(userType: String, rptType: Int, monthNo: Int, date: String?): List<NetworkUserBirthday> =
        service.getBirthdays(userType, rptType, monthNo, date).unwrapPayload { usersBirthday ?: emptyList() }
}
