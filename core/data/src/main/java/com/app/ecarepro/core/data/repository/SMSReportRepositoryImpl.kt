package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.smsreport.SMSReportItem
import com.app.ecarepro.core.domain.model.smsreport.SMSTypeItem
import com.app.ecarepro.core.domain.repository.SMSReportRepository
import com.app.ecarepro.core.network.SMSReportRemoteDataSource
import com.app.ecarepro.core.network.model.smsreport.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SMSReportRepositoryImpl @Inject constructor(
    private val dataSource: SMSReportRemoteDataSource,
) : SMSReportRepository {

    override fun getSMSTypes(): Flow<Result<List<SMSTypeItem>>> = asResultFlow {
        dataSource.getSMSTypes().map { it.toDomainModel() }
    }

    override fun getSMSReport(fromDate: String, tillDate: String, smsType: Int): Flow<Result<List<SMSReportItem>>> = asResultFlow {
        dataSource.getSMSReport(fromDate, tillDate, smsType).map { it.toDomainModel() }
    }
}
