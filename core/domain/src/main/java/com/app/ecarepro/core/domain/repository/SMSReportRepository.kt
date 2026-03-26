package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.smsreport.SMSReportItem
import com.app.ecarepro.core.domain.model.smsreport.SMSTypeItem
import kotlinx.coroutines.flow.Flow

interface SMSReportRepository {
    fun getSMSTypes(): Flow<Result<List<SMSTypeItem>>>
    fun getSMSReport(fromDate: String, tillDate: String, smsType: Int): Flow<Result<List<SMSReportItem>>>
}
