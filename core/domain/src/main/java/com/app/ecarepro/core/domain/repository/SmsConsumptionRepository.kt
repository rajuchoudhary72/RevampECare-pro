package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.sms.DailyConsumptionItem
import kotlinx.coroutines.flow.Flow

interface SmsConsumptionRepository {
    fun getSMSConsumption(fromDate: String, toDate: String): Flow<Result<List<DailyConsumptionItem>>>
}
