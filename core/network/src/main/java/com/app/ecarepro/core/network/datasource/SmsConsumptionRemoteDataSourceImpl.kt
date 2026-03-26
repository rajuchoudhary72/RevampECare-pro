package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.SmsConsumptionRemoteDataSource
import com.app.ecarepro.core.network.model.sms.NetworkSMSConsumptionResponse
import com.app.ecarepro.core.network.retrofit.service.SmsService
import javax.inject.Inject

internal class SmsConsumptionRemoteDataSourceImpl @Inject constructor(
    private val service: SmsService,
) : SmsConsumptionRemoteDataSource {
    override suspend fun getSMSConsumption(fromDate: String, toDate: String): NetworkSMSConsumptionResponse =
        service.getSMSConsumption(fromDate, toDate)
}
