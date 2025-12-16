package com.app.ecarepro.designsystem.core.component

import java.util.UUID

/**
 * Represents a downloadable file attachment
 */
data class ECAttachment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val url: String
)
