package com.app.ecarepro.core.network.model.smsreport

import com.app.ecarepro.core.domain.model.smsreport.SMSReportItem
import com.app.ecarepro.core.domain.model.smsreport.SMSTypeItem
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSMSTypeResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("smsType") val smsType: List<NetworkSMSTypeItem>? = null,
) : NetworkResponse

@Serializable
data class NetworkSMSTypeItem(
    @SerialName("typeID") val typeID: Int? = null,
    @SerialName("subject") val subject: String? = null,
    @SerialName("templates") val templates: String? = null,
    @SerialName("isDefault") val isDefault: Boolean? = null,
)

@Serializable
data class NetworkSMSReportResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("smSs") val smSs: List<NetworkSMSItem>? = null,
) : NetworkResponse

@Serializable
data class NetworkSMSItem(
    @SerialName("smsType") val smsType: String? = null,
    @SerialName("text") val text: String? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("senderName") val senderName: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("sentOn") val sentOn: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusOn") val statusOn: String? = null,
    @SerialName("receiver") val receiver: NetworkSMSReceiver? = null,
)

@Serializable
data class NetworkSMSReceiver(
    @SerialName("receiverID") val receiverID: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("childName") val childName: String? = null,
    @SerialName("className") val className: String? = null,
)

fun NetworkSMSTypeItem.toDomainModel() = SMSTypeItem(
    typeID = typeID ?: 0,
    subject = subject ?: "",
)

fun NetworkSMSItem.toDomainModel() = SMSReportItem(
    receiverName = receiver?.name ?: "N/A",
    receiverDesignation = receiver?.designation ?: "",
    receiverPhotoUrl = receiver?.photo,
    messageText = text ?: "",
    smsTypeName = smsType ?: "",
    status = status ?: "",
    isSent = status?.lowercase() == "sent",
    senderName = senderName ?: "",
    senderPhotoUrl = photo,
    sentOnDate = sentOn ?: "",
)
