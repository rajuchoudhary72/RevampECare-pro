package com.app.ecarepro.core.network.model.fee

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive

// ─── Requests ───

@Serializable
data class NetworkFeeReceiptRequest(
    val senderid: String,
    val username: String,
    val sessionid: Int,
)

@Serializable
data class NetworkReceiptDownloadRequest(
    val senderid: String,
    val recid: String,
    val sessionid: Int,
    val stid: Int,
)

@Serializable
data class NetworkFeeCollectionRequest(
    @SerialName("DateFrom") val dateFrom: String,
    @SerialName("DateTo") val dateTo: String,
    val senderid: String,
)

@Serializable
data class NetworkFeeReportRequest(
    @SerialName("DateFrom") val dateFrom: String,
    @SerialName("DateTo") val dateTo: String,
    val senderid: String,
    val classid: String,
    val feetypeid: String,
    val schoolid: String,
    val sectionid: String,
    val installid: String,
)

// ─── Fee Receipt ───

@Serializable
data class NetworkFeeReceiptResponse(
    @SerialName("session_data") val sessionData: List<NetworkFeeSession>? = null,
    @SerialName("receipt_data") val receiptData: List<NetworkFeeReceipt>? = null,
)

@Serializable
data class NetworkFeeSession(
    val yrid: Int? = null,
    val yearname: String? = null,
    val active: String? = null,
)

@Serializable
data class NetworkFeeReceipt(
    val recno: String? = null,
    val recid: String? = null,
    val paymode: String? = null,
    val feetypeid: String? = null,
    val recdate: String? = null,
    val paymodekey: String? = null,
    val installment: String? = null,
    val paidamt: String? = null,
    val paymodevalue: String? = null,
)

@Serializable
data class NetworkReceiptDownloadResponse(
    val bytedata: String? = null,
)

// ─── Fee Collection ───

@Serializable
data class NetworkFeeCollectionItem(
    @SerialName("date") val date: String? = null,
    @SerialName("amount") val amount: String? = null,
)

// ─── Fee Report Filters ───

@Serializable
data class NetworkFeeReportFilterResponse(
    val classes: List<NetworkFilterClass>? = null,
    val schools: List<NetworkFilterSchool>? = null,
    val feetype: List<NetworkFilterFeeType>? = null,
    val installment: List<NetworkFilterInstallment>? = null,
    val sections: List<NetworkFilterSection>? = null,
)

@Serializable
data class NetworkFilterClass(val classid: String? = null, val classname: String? = null)

@Serializable
data class NetworkFilterSchool(val schoolid: String? = null, val schoolname: String? = null)

@Serializable
data class NetworkFilterFeeType(val feetypeid: String? = null, val feetypename: String? = null)

@Serializable
data class NetworkFilterInstallment(val installid: String? = null, val installmentname: String? = null)

@Serializable
data class NetworkFilterSection(val sectionid: String? = null, val sectionname: String? = null)

// ─── Fee Defaulter ───

@Serializable
data class NetworkDefaulterItem(
    val sno: Int? = null,
    val studentname: String? = null,
    val classsection: String? = null,
    val admno: String? = null,
    val contactno: String? = null,
    val amount: JsonElement? = null,
    val installmentname: String? = null,
)

fun NetworkDefaulterItem.amountAsDouble(): Double {
    val element = amount ?: return 0.0
    return runCatching {
        when {
            element is JsonPrimitive && element.isString -> element.content.toDouble()
            element is JsonPrimitive -> element.double
            else -> 0.0
        }
    }.getOrDefault(0.0)
}

// ─── Fee Estimate ───

@Serializable
data class NetworkEstimateItem(
    @SerialName("HeadName") val headName: String? = null,
    @SerialName("ActualAmount") val actualAmount: String? = null,
    @SerialName("Concession") val concession: String? = null,
    @SerialName("ReceivedAmount") val receivedAmount: String? = null,
    val duesamount: String? = null,
)

// ─── Fee Certificate ───

@Serializable
data class FeeCertificateRequest(
    @SerialName("SenderId") val senderId: String,
    @SerialName("SessionId") val sessionId: Int,
    @SerialName("ParentName") val parentName: String,
    @SerialName("stid") val stid: String,
    @SerialName("SessionName") val sessionName: String,
)

@Serializable
data class NetworkFeeCertificateResponse(
    @SerialName("status") val status: Boolean? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("bytedata") val bytedata: String? = null,
)
