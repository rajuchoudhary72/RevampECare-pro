package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.fee.DefaulterDomain
import com.app.ecarepro.core.domain.model.fee.EstimateDomain
import com.app.ecarepro.core.domain.model.fee.FeeCollection
import com.app.ecarepro.core.domain.model.fee.FeeReceiptDomain
import com.app.ecarepro.core.domain.model.fee.FeeReportFiltersDomain
import com.app.ecarepro.core.domain.model.fee.FeeSessionDomain
import kotlinx.coroutines.flow.Flow

interface FeeRepository {
    fun getCollections(
        dateFrom: String,
        dateTo: String,
    ): Flow<Result<List<FeeCollection>>>

    fun getReceipts(
        sessionId: Int,
    ): Flow<Result<Pair<List<FeeSessionDomain>, List<FeeReceiptDomain>>>>

    fun downloadReceipt(
        recId: String,
        sessionId: Int,
        stId: Int,
    ): Flow<Result<String>>

    fun getReportFilters(): Flow<Result<FeeReportFiltersDomain>>

    fun getDefaulterReport(
        dateFrom: String,
        dateTo: String,
        classId: String,
        feeTypeId: String,
        schoolId: String,
        sectionId: String,
        installId: String,
    ): Flow<Result<List<DefaulterDomain>>>

    fun getEstimateReport(
        dateFrom: String,
        dateTo: String,
        classId: String,
        feeTypeId: String,
        schoolId: String,
        sectionId: String,
        installId: String,
    ): Flow<Result<List<EstimateDomain>>>

    fun getCertificate(sessionId: Int, sessionName: String): Flow<Result<String?>>
}
