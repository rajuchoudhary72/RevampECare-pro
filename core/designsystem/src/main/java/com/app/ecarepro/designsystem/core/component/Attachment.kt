package com.app.ecarepro.designsystem.core.component

import java.util.UUID

/**
 * Represents a downloadable file attachment
 */
data class ECAttachment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val url: String,
    val fileType: String = "pdf",
    val size: String? = null, // Optional file size (e.g., "2.5 MB")
    val uploadedDate: String? = null // Optional upload date
)
