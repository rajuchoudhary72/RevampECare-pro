package com.app.ecarepro.core.network.model.feed

import com.app.ecarepro.core.domain.model.FeedModule
import com.app.ecarepro.core.domain.model.FeedResponse
import com.app.ecarepro.core.domain.model.FeedUpdate
import com.app.ecarepro.core.domain.model.GalleryUpdate
import com.app.ecarepro.core.domain.model.toFeedType
import com.app.ecarepro.core.domain.model.toTempUserName
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkFeedResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("total") val total: Int = 0,
    @SerialName("serverDateTime") val serverDateTime: String = "",
    @SerialName("modules") val modules: List<NetworkFeedModule> = emptyList(),
    @SerialName("updates") val updates: List<NetworkFeedUpdate> = emptyList(),
) : NetworkResponse

@Serializable
data class NetworkFeedModule(
    @SerialName("menuID") val menuID: Int = 0,
    @SerialName("moduleName") val moduleName: String = "",
)

@Serializable
data class NetworkFeedUpdate(
    @SerialName("menuID") val menuID: Int = 0,
    @SerialName("module") val module: String = "",
    @SerialName("id") val id: String = "",
    @SerialName("caption") val caption: String = "",
    @SerialName("hasAttachment") val hasAttachment: Boolean = false,
    @SerialName("updtedOn") val updtedOn: String = "",
    @SerialName("msgDTL") val msgDTL: String? = null,
    @SerialName("galleryUpdate") val galleryUpdate: NetworkFeedGalleryUpdate? = null,
    @SerialName("webLink") val webLink: String = "",
    @SerialName("color") val color: String = "",
    @SerialName("icon") val icon: String = "",
    @SerialName("attachmentURL") val attachmentURL: String? = null,
)

@Serializable
data class NetworkFeedGalleryUpdate(
    @SerialName("s_MdlID") val sMdlID: Int = 0,
    @SerialName("subModule") val subModule: String? = null,
    @SerialName("total") val total: Int = 0,
    @SerialName("fileURL") val fileURL: String = "",
    @SerialName("fileNames") val fileNames: List<String> = emptyList(),
)

fun NetworkFeedModule.toDomainModel() = FeedModule(
    menuID = menuID,
    moduleName = moduleName,
)

fun NetworkFeedGalleryUpdate.toDomainModel() = GalleryUpdate(
    sMdlID = sMdlID,
    subModule = subModule,
    total = total,
    fileURL = fileURL,
    fileNames = fileNames,
)

fun NetworkFeedUpdate.toDomainModel(): FeedUpdate {
    val feedType = menuID.toFeedType()
    val resolvedColor = color.takeIf { it.isNotBlank() } ?: feedType.colorHex
    val resolvedName = module.takeIf { it.isNotBlank() } ?: menuID.toTempUserName()
    return FeedUpdate(
        menuID = menuID,
        module = module,
        id = id,
        caption = caption,
        hasAttachment = hasAttachment,
        updatedOn = updtedOn,
        msgDTL = msgDTL,
        galleryUpdate = galleryUpdate?.toDomainModel(),
        webLink = webLink,
        feedType = feedType,
        attachments = emptyList(),
        userName = resolvedName,
        userImageUrl = null,
        accentColor = resolvedColor,
        attachmentURL = attachmentURL,
        iconName = icon,
    )
}

fun NetworkFeedResponse.toDomainModel() = FeedResponse(
    errorCode = errorCode,
    status = status,
    message = message,
    total = total,
    serverDateTime = serverDateTime,
    modules = modules.map { it.toDomainModel() },
    updates = updates.map { it.toDomainModel() },
)
