package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.sms.NetworkSMSConsumptionResponse

interface SmsConsumptionRemoteDataSource {
    suspend fun getSMSConsumption(fromDate: String, toDate: String): NetworkSMSConsumptionResponse
}
