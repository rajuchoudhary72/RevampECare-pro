package com.app.ecarepro.core.domain.model

import android.webkit.MimeTypeMap
import java.io.File

enum class DocType(
    vararg val extensions: String,
) {
    PDF(".pdf"),
    DOC(".doc", ".docx"),
    IMAGE(".png", ".jpg", ".jpeg", ".webp"),
    VIDEO(".mp4", ".mov", ".avi", ".mkv"),

    AUDIO(".mp3", ".wav", ".ogg", ".m4a"),
    SPREADSHEET(".xls", ".xlsx", ".csv");

    /**
     * Helper function to quickly check if this DocType is an IMAGE.
     *
     * @return True if the type is IMAGE, false otherwise.
     */
    fun isImage(): Boolean = this == IMAGE

    /**
     * Helper function to quickly check if this DocType is a VIDEO.
     *
     * @return True if the type is VIDEO, false otherwise.
     */
    fun isVideo(): Boolean = this == VIDEO

    companion object {
        /**
         * Returns the matching DocType for the given file extension.
         *
         * @param extension The file extension (e.g., ".pdf", ".jpg").
         * @return The corresponding [DocType] or null if no match is found.
         */
        fun fromExtension(extension: String): DocType? {
            return entries.find { docType ->
                docType.extensions.any { it.equals(extension, ignoreCase = true) }
            }
        }

        /**
         * Determines the DocType from a given URL by extracting its file extension.
         *
         * @param url The full URL string of the file.
         * @return The corresponding [DocType] or null if the URL has no recognizable extension.
         */
        fun fromUrl(url: String): DocType? {
            val extension = File(url).extension
            return if (extension.isNotBlank()) {
                fromExtension(".$extension")
            } else {
                null
            }
        }

        fun fromFile(file: File): DocType? {
            val extension = file.extension
            return if (extension.isNotBlank()) {
                fromExtension(".$extension")
            } else {
                null
            }
        }

        /**
         * RECOMMENDED: Determines the DocType from a given MIME type.
         * This is useful for handling files from content resolvers or network responses.
         *
         * @param mimeType The MIME type string (e.g., "image/jpeg", "application/pdf").
         * @return The corresponding [DocType] or null if no match is found.
         */
        fun fromMimeType(mimeType: String): DocType? {
            // Use Android's MimeTypeMap to get the canonical extension for a given MIME type.
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            return if (extension != null) {
                fromExtension(".$extension")
            } else {
                null
            }
        }
    }
}