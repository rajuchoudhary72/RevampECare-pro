package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.fee.DefaulterDomain
import com.app.ecarepro.core.domain.model.fee.EstimateDomain
import com.app.ecarepro.core.domain.model.fee.FeeCollection
import com.app.ecarepro.core.domain.model.fee.FeeReceiptDomain
import com.app.ecarepro.core.domain.model.fee.FeeReportFiltersDomain
import com.app.ecarepro.core.domain.model.fee.FeeSessionDomain
import com.app.ecarepro.core.domain.model.fee.FilterItemDomain
import com.app.ecarepro.core.domain.repository.FeeRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.network.FeeRemoteDataSource
import com.app.ecarepro.core.network.model.fee.NetworkFeeReportRequest
import com.app.ecarepro.core.network.model.fee.amountAsDouble
import kotlinx.coroutines.flow.Flow
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

internal class FeeRepositoryImpl @Inject constructor(
    private val remoteDataSource: FeeRemoteDataSource,
    private val userRepository: UserRepository,
) : FeeRepository {

    private val baseUrl = "https://pay.franciscanwebsolutions.com/"

    private fun formatCurrency(amountStr: String?): String {
        val amount = amountStr?.toDoubleOrNull() ?: 0.0
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return "₹ ${formatter.format(amount)}"
    }

    override fun getCollections(
        dateFrom: String,
        dateTo: String,
    ): Flow<Result<List<FeeCollection>>> = asResultFlow {

        val senderId = userRepository.getActiveUser()?.schoolCode ?: ""
        val items = remoteDataSource.getCollections(
            baseUrl = baseUrl,
            dateFrom = dateFrom,
            dateTo = dateTo,
            senderId = senderId,
        )
        val displayFormatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.ENGLISH)
        items.map { item ->
            val parsedDate = runCatching { LocalDate.parse(item.date) }.getOrNull()
            val formattedDate = parsedDate?.format(displayFormatter) ?: (item.date ?: "")
            val dayOfWeek = parsedDate?.dayOfWeek?.getDisplayName(TextStyle.FULL, Locale.ENGLISH) ?: ""
            val amount = item.amount?.toDoubleOrNull() ?: 0.0
            val formattedAmount = formatCurrency(item.amount)
            FeeCollection(
                date = item.date ?: "",
                amount = amount,
                formattedDate = formattedDate,
                dayOfWeek = dayOfWeek,
                formattedAmount = formattedAmount,
            )
        }
    }

    override fun getReceipts(
        sessionId: Int,
    ): Flow<Result<Pair<List<FeeSessionDomain>, List<FeeReceiptDomain>>>> = asResultFlow {

        val user = userRepository.getActiveUser()
        val senderId = user?.schoolCode ?: ""
        val username = user?.name ?: ""
        val stId = user?.userID ?: 0
        val response = remoteDataSource.getReceipts(
            baseUrl = baseUrl,
            senderId = senderId,
            username = username,
            sessionId = sessionId,
        )
        val sessions = response.sessionData?.map { session ->
            FeeSessionDomain(
                yrid = session.yrid ?: 0,
                yearname = session.yearname ?: "",
                isActive = session.active == "1",
            )
        } ?: emptyList()
        val receipts = response.receiptData?.map { receipt ->
            FeeReceiptDomain(
                recId = receipt.recid ?: "",
                recDate = receipt.recdate ?: "",
                paidAmount = formatCurrency(receipt.paidamt),
                receiptNumber = receipt.recno ?: "",
                paymentMode = receipt.paymodevalue ?: receipt.paymode ?: "",
                installment = receipt.installment ?: "",
                stId = stId,
            )
        } ?: emptyList()
        Pair(sessions, receipts)
    }

    override fun downloadReceipt(
        recId: String,
        sessionId: Int,
        stId: Int,
    ): Flow<Result<String>> = asResultFlow {

        val user = userRepository.getActiveUser()
        val senderId = user?.schoolCode ?: ""
        val response = remoteDataSource.downloadReceipt(
            baseUrl = baseUrl,
            senderId = senderId,
            recId = recId,
            sessionId = sessionId,
            stId = stId,
        )
        response.bytedata ?: ""
    }

    override fun getReportFilters(): Flow<Result<FeeReportFiltersDomain>> = asResultFlow {

        val senderId = userRepository.getActiveUser()?.schoolCode ?: ""
        val response = remoteDataSource.getReportFilters(
            baseUrl = baseUrl,
            senderId = senderId,
        )
        FeeReportFiltersDomain(
            classes = response.classes?.map {
                FilterItemDomain(id = it.classid?.trim() ?: "", name = it.classname?.trim() ?: "")
            } ?: emptyList(),
            schools = response.schools?.map {
                FilterItemDomain(id = it.schoolid?.trim() ?: "", name = it.schoolname?.trim() ?: "")
            } ?: emptyList(),
            feeTypes = response.feetype?.map {
                FilterItemDomain(id = it.feetypeid?.trim() ?: "", name = it.feetypename?.trim() ?: "")
            } ?: emptyList(),
            installments = response.installment?.map {
                FilterItemDomain(id = it.installid?.trim() ?: "", name = it.installmentname?.trim() ?: "")
            } ?: emptyList(),
            sections = response.sections?.map {
                FilterItemDomain(id = it.sectionid?.trim() ?: "", name = it.sectionname?.trim() ?: "")
            } ?: emptyList(),
        )
    }

    override fun getDefaulterReport(
        dateFrom: String,
        dateTo: String,
        classId: String,
        feeTypeId: String,
        schoolId: String,
        sectionId: String,
        installId: String,
    ): Flow<Result<List<DefaulterDomain>>> = asResultFlow {

        val senderId = userRepository.getActiveUser()?.schoolCode ?: ""
        val items = remoteDataSource.getDefaulterReport(
            baseUrl = baseUrl,
            request = NetworkFeeReportRequest(
                dateFrom = dateFrom,
                dateTo = dateTo,
                senderid = senderId,
                classid = classId,
                feetypeid = feeTypeId,
                schoolid = schoolId,
                sectionid = sectionId,
                installid = installId,
            ),
        )
        items.map { item ->
            DefaulterDomain(
                nameWithClass = "${item.studentname?.trim() ?: ""}, ${item.classsection?.trim() ?: ""}",
                admNo = item.admno ?: "",
                contactNo = item.contactno ?: "",
                amount = item.amountAsDouble(),
                installmentName = item.installmentname ?: "",
            )
        }
    }

    override fun getEstimateReport(
        dateFrom: String,
        dateTo: String,
        classId: String,
        feeTypeId: String,
        schoolId: String,
        sectionId: String,
        installId: String,
    ): Flow<Result<List<EstimateDomain>>> = asResultFlow {

        val senderId = userRepository.getActiveUser()?.schoolCode ?: ""
        val items = remoteDataSource.getEstimateReport(
            baseUrl = baseUrl,
            request = NetworkFeeReportRequest(
                dateFrom = dateFrom,
                dateTo = dateTo,
                senderid = senderId,
                classid = classId,
                feetypeid = feeTypeId,
                schoolid = schoolId,
                sectionid = sectionId,
                installid = installId,
            ),
        )
        items.map { item ->
            EstimateDomain(
                headName = item.headName ?: "",
                actualAmount = item.actualAmount?.toDoubleOrNull() ?: 0.0,
                concession = item.concession?.toDoubleOrNull() ?: 0.0,
                receivedAmount = item.receivedAmount?.toDoubleOrNull() ?: 0.0,
                duesAmount = item.duesamount?.toDoubleOrNull() ?: 0.0,
            )
        }
    }

    override fun getCertificate(sessionId: Int, sessionName: String): Flow<Result<String?>> = asResultFlow {
        val user = userRepository.getActiveUser()
        val senderId = user?.schoolCode ?: ""
        val stid = (user?.userID ?: 0).toString()
        remoteDataSource.getCertificate(
            baseUrl = baseUrl,
            senderId = senderId,
            sessionId = sessionId,
            parentName = "",
            stid = stid,
            sessionName = sessionName,
        )
    }
}
