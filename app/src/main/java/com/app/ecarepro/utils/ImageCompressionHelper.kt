package com.app.ecarepro.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.fragment.app.FragmentManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.roundToInt

/**
 * Helper class for image compression tasks
 */
class ImageCompressionHelper(private val context: Context) {

    companion object {
        // API limit in bytes (28MB)
        const val MAX_PAYLOAD_SIZE = 28 * 1024 * 1024

        // Compression options
        const val SIZE_SMALL = 0   // 40% of original
        const val SIZE_MEDIUM = 1  // 70% of original
        const val SIZE_LARGE = 2   // 85% of original
        const val SIZE_ACTUAL = 3  // 100% of original
    }

    /**
     * Calculate the total size of multiple images
     * @param imageUris List of image URIs to calculate
     * @return Combined size in bytes
     */
    fun calculateTotalImageSize(imageUris: List<Uri>): Long {
        var totalSize = 0L

        for (uri in imageUris) {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                totalSize += inputStream.available().toLong()
            }
        }

        return totalSize
    }

    /**
     * Check if the combined size of images exceeds API limit
     * @param imageUris List of image URIs to check
     * @return true if size exceeds limit
     */
    fun exceedsPayloadLimit(imageUris: List<Uri>): Boolean {
        return calculateTotalImageSize(imageUris) > MAX_PAYLOAD_SIZE
    }

    /**
     * Convert an image to a base64 string
     * @param imageUri URI of the image
     * @param compressionLevel Compression level (0-100)
     * @return Base64 encoded string
     */
    fun imageToBase64(imageUri: Uri, compressionLevel: Int = 100): String {
        val inputStream: InputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Cannot open image")

        // Decode bitmap with options to get original size
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        // Compress to byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, compressionLevel, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Convert to Base64
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /**
     * Compress an image to a specific percentage of the original size
     * @param imageUri URI of the original image
     * @param sizeOption Selected size option (SMALL, MEDIUM, LARGE, ACTUAL)
     * @return URI of the compressed image file
     */
    fun compressImage(imageUri: Uri, sizeOption: Int): Uri {
        // If keeping original size, just return the original
        if (sizeOption == SIZE_ACTUAL) {
            return imageUri
        }

        // Get input stream from URI
        val inputStream: InputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Cannot open image")

        // Calculate original file size
        val originalFileSize = inputStream.available().toLong()

        // Calculate target size based on percentage of original
        val targetSize = when(sizeOption) {
            SIZE_SMALL -> (originalFileSize * 0.4).toLong()   // 40% of original
            SIZE_MEDIUM -> (originalFileSize * 0.6).toLong()  // 70% of original
            SIZE_LARGE -> (originalFileSize * 0.8).toLong()  // 85% of original
            SIZE_ACTUAL -> originalFileSize  // 100% of original
            else -> originalFileSize
        }

        // Reset input stream position
        inputStream.close()
        val resetInputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Cannot open image")

        // Decode bitmap with options to get original size
        val originalBitmap = BitmapFactory.decodeStream(resetInputStream)
        resetInputStream.close()

        // Original dimensions
        val originalWidth = originalBitmap.width
        val originalHeight = originalBitmap.height

        // Try different quality levels to get close to target size
        var quality = 95  // Start with high quality
        var compressedByteArray: ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()

        // First compression attempt
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        compressedByteArray = byteArrayOutputStream.toByteArray()

        // If we need scaling, determine the size ratio
        var scaleFactor = 1.0f
        if (compressedByteArray.size > targetSize * 1.2) {
            // If the image is still way too big, we need to scale down dimensions
            val targetPixels = originalWidth * originalHeight * (targetSize.toFloat() / compressedByteArray.size)
            scaleFactor = Math.sqrt((targetPixels / (originalWidth * originalHeight)).toDouble()).toFloat()

            // Create a new scaled bitmap
            val scaledWidth = (originalWidth * scaleFactor).roundToInt()
            val scaledHeight = (originalHeight * scaleFactor).roundToInt()

            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true)

            // Reset and try again with the scaled bitmap
            byteArrayOutputStream.reset()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            compressedByteArray = byteArrayOutputStream.toByteArray()
        }

        // Binary search for quality level
        while (quality > 10 && compressedByteArray.size > targetSize * 1.05) {
            quality -= 5
            byteArrayOutputStream.reset()

            if (scaleFactor < 1.0f) {
                // Use the scaled bitmap if we created one
                val scaledWidth = (originalWidth * scaleFactor).roundToInt()
                val scaledHeight = (originalHeight * scaleFactor).roundToInt()
                val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true)
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            } else {
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            }

            compressedByteArray = byteArrayOutputStream.toByteArray()
        }

        // Save to a temporary file
        val tempFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(tempFile).use { fos ->
            fos.write(compressedByteArray)
        }

        // Return URI for the temp file
        return Uri.fromFile(tempFile)
    }

    /**
     * Show the compression options dialog
     * @param fragmentManager FragmentManager to show the dialog
     * @param imageUris List of image URIs
     * @param onCompressionSelected Callback for when user selects a compression option
     */
    fun showCompressionDialog(
        fragmentManager: FragmentManager,
        imageUris: List<Uri>,
        onCompressionSelected: (Int) -> Unit
    ) {
        val totalSize = calculateTotalImageSize(imageUris)
        val dialog = ImageCompressionDialog.newInstance(totalSize, onCompressionSelected)
        dialog.show(fragmentManager, ImageCompressionDialog.TAG)
    }
}