package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.smsreport.NetworkSMSItem
import com.app.ecarepro.core.network.model.smsreport.NetworkSMSTypeItem

interface SMSReportRemoteDataSource {
    suspend fun getSMSTypes(): List<NetworkSMSTypeItem>
    suspend fun getSMSReport(fromDate: String, tillDate: String, smsType: Int): List<NetworkSMSItem>
}
