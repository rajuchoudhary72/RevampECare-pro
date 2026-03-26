package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.SMSReportRemoteDataSource
import com.app.ecarepro.core.network.model.smsreport.NetworkSMSItem
import com.app.ecarepro.core.network.model.smsreport.NetworkSMSTypeItem
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.ReportService
import javax.inject.Inject

internal class SMSReportRemoteDataSourceImpl @Inject constructor(
    private val service: ReportService,
) : SMSReportRemoteDataSource {

    override suspend fun getSMSTypes(): List<NetworkSMSTypeItem> =
        service.getSMSTypes().unwrapPayload { smsType ?: emptyList() }

    override suspend fun getSMSReport(fromDate: String, tillDate: String, smsType: Int): List<NetworkSMSItem> =
        service.getSMSReport(fromDate, tillDate, smsType).unwrapPayload { smSs ?: emptyList() }
}
