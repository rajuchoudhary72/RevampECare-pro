package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.sms.DailyConsumptionItem
import com.app.ecarepro.core.domain.repository.SmsConsumptionRepository
import com.app.ecarepro.core.network.SmsConsumptionRemoteDataSource
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

internal class SmsConsumptionRepositoryImpl @Inject constructor(
    private val dataSource: SmsConsumptionRemoteDataSource,
) : SmsConsumptionRepository {

    override fun getSMSConsumption(fromDate: String, toDate: String): Flow<Result<List<DailyConsumptionItem>>> =
        asResultFlow {
            val inputFmt = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            val displayFmt = SimpleDateFormat("dd MMMM", Locale.ENGLISH)
            val dayOfWeekFmt = SimpleDateFormat("EEEE", Locale.ENGLISH)
            dataSource.getSMSConsumption(fromDate, toDate).dateWise?.map { item ->
                val parsed = runCatching { inputFmt.parse(item.sentOn.orEmpty()) }.getOrNull()
                DailyConsumptionItem(
                    sentOn = item.sentOn.orEmpty(),
                    displayDate = parsed?.let { displayFmt.format(it) } ?: item.sentOn.orEmpty(),
                    displayDayOfWeek = parsed?.let { dayOfWeekFmt.format(it) } ?: "",
                    count = item.count ?: 0,
                )
            } ?: emptyList()
        }
}
