package com.app.ecarepro.core.domain.model

import javax.annotation.concurrent.Immutable

@Immutable
data class FeedResponse(
    val errorCode: Int,
    val status: String,
    val message: String,
    val total: Int,
    val serverDateTime: String,
    val updates: List<FeedUpdate>
)

@Immutable
data class FeedUpdate(
    val menuID: Int,
    val chMenuID: Int,
    val sbChMenuID: Int,
    val module: String,
    val id: String,
    val caption: String,
    val hasAttachment: Boolean,
    val updatedOn: String,
    val msgDTL: String?,
    val galleryUpdate: GalleryUpdate?,
    val webLink: String,
    val feedType: FeedType,
    val attachments: List<Attachment>
)

@Immutable
data class GalleryUpdate(
    val sMdlID: Int,
    val subModule: String?,
    val total: Int,
    val fileURL: String,
    val fileNames: List<String>
)

@Immutable
data class Attachment(
    val fileName: String,
    val fileUrl: String,
    val fileType: FileType
)

enum class FeedType(val displayName: String, val colorHex: String) {
    CIRCULAR("Circular", "#4CAF50"),  // Green
    NOTICE("Notice", "#FFC107"),      // Yellow/Amber
    PHOTO("Photo", "#2196F3"),        // Blue
    UNKNOWN("Update", "#9E9E9E")      // Grey
}

enum class FileType {
    IMAGE,
    PDF,
    DOC,
    DOCX,
    XLS,
    XLSX,
    PPT,
    PPTX,
    TXT,
    UNKNOWN
}
