package com.app.ecarepro.core.domain.model

import javax.annotation.concurrent.Immutable

@Immutable
data class FeedModule(
    val menuID: Int,
    val moduleName: String,
)

@Immutable
data class FeedResponse(
    val errorCode: Int,
    val status: String,
    val message: String,
    val total: Int,
    val serverDateTime: String,
    val modules: List<FeedModule>,
    val updates: List<FeedUpdate>,
)

@Immutable
data class FeedUpdate(
    val menuID: Int,
    val module: String,
    val id: String,
    val caption: String,
    val hasAttachment: Boolean,
    val updatedOn: String,
    val msgDTL: String?,
    val galleryUpdate: GalleryUpdate?,
    val webLink: String,
    val feedType: FeedType,
    val attachments: List<Attachment>,
    val userName: String = "School",
    val userImageUrl: String? = null,
    val accentColor: String = "#4CAF50",
    val attachmentURL: String? = null,
    val iconName: String = "",
) {
    val uniqueKey: String get() = "${menuID}_${id}"
}

@Immutable
data class GalleryUpdate(
    val sMdlID: Int,
    val subModule: String?,
    val total: Int,
    val fileURL: String,
    val fileNames: List<String>,
)

@Immutable
data class Attachment(
    val fileName: String,
    val fileUrl: String,
    val fileType: FileType,
)

enum class FeedType(val displayName: String, val colorHex: String) {
    MESSAGE("Message", "#2196F3"),
    SCHOOL_NOTICE("School Notice", "#FFC107"),
    CLASS_NOTICE("Class Notice", "#FF9800"),
    CIRCULAR("Circular", "#4CAF50"),
    LEAVE("Leave Requests", "#9C27B0"),
    PHOTO("Photo Albums", "#00BCD4"),
    VIDEO("Video Albums", "#F44336"),
    NOTICE("Notice", "#FFC107"),   // kept for backward compat with previews
    UNKNOWN("Update", "#9E9E9E"),
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
    UNKNOWN,
}

fun Int.toFeedType(): FeedType = when (this) {
    2 -> FeedType.MESSAGE
    52 -> FeedType.SCHOOL_NOTICE
    53 -> FeedType.CLASS_NOTICE
    55 -> FeedType.CIRCULAR
    59 -> FeedType.LEAVE
    71 -> FeedType.PHOTO
    72 -> FeedType.VIDEO
    else -> FeedType.UNKNOWN
}

fun Int.toTempUserName(): String = when (this) {
    2 -> "Message"
    52, 53 -> "School"
    55 -> "School"
    59 -> "Leave"
    71, 72 -> "Gallery"
    else -> "School"
}

fun GalleryUpdate.toAttachments(): List<Attachment> {
    return fileNames.map { fileName ->
        Attachment(
            fileName = fileName,
            fileUrl = "$fileURL$fileName",
            fileType = FileType.IMAGE,
        )
    }
}
