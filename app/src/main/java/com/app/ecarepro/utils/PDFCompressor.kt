package com.app.ecarepro.utils


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.graphics.pdf.PdfDocument
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import kotlin.math.roundToInt

class PDFCompressor {

    companion object {
        private const val MAX_SIZE_MB = 30
        private const val COMPRESSION_RATIO = 0.6f // 40% compression (60% of original)
        private const val BYTES_IN_MB = 1024 * 1024
    }

    /**
     * Compresses a PDF file if it's larger than 30MB
     * @param context Android context
     * @param inputFile Input PDF file
     * @param outputFile Output compressed PDF file
     * @return CompressonResult with success status and final file size
     */
    suspend fun compressPDF(
        context: Context,
        inputFile: File,
        outputFile: File
    ): CompressionResult = withContext(Dispatchers.IO) {

        val inputSizeMB = inputFile.length() / BYTES_IN_MB.toDouble()

        // Check if compression is needed
        if (inputSizeMB <= MAX_SIZE_MB) {
            // Just copy the file if it's already small enough
            inputFile.copyTo(outputFile, overwrite = true)
            return@withContext CompressionResult(
                success = true,
                originalSizeMB = inputSizeMB,
                compressedSizeMB = inputSizeMB,
                message = "File size is already under ${MAX_SIZE_MB}MB. No compression needed."
            )
        }

        try {
            val result = performCompression(inputFile, outputFile)

            val compressedSizeMB = outputFile.length() / BYTES_IN_MB.toDouble()

            CompressionResult(
                success = true,
                originalSizeMB = inputSizeMB,
                compressedSizeMB = compressedSizeMB,
                message = "PDF compressed successfully from ${String.format("%.2f", inputSizeMB)}MB to ${String.format("%.2f", compressedSizeMB)}MB"
            )

        } catch (e: Exception) {
            CompressionResult(
                success = false,
                originalSizeMB = inputSizeMB,
                compressedSizeMB = 0.0,
                message = "Compression failed: ${e.message}"
            )
        }
    }

    private suspend fun performCompression(inputFile: File, outputFile: File) = withContext(Dispatchers.IO) {
        val parcelFileDescriptor = ParcelFileDescriptor.open(inputFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)

        val pdfDocument = PdfDocument()

        try {
            for (pageIndex in 0 until pdfRenderer.pageCount) {
                val page = pdfRenderer.openPage(pageIndex)

                // Calculate compressed dimensions (40% reduction in quality)
                val compressedWidth = (page.width * COMPRESSION_RATIO).roundToInt()
                val compressedHeight = (page.height * COMPRESSION_RATIO).roundToInt()

                // Create bitmap with compressed dimensions
                val bitmap = Bitmap.createBitmap(compressedWidth, compressedHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawColor(android.graphics.Color.WHITE) // White background

                // Render page to compressed bitmap
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                // Create new page in output document
                val pageInfo = PdfDocument.PageInfo.Builder(compressedWidth, compressedHeight, pageIndex + 1).create()
                val pdfPage = pdfDocument.startPage(pageInfo)
                val pdfCanvas = pdfPage.canvas

                // Draw compressed bitmap to PDF page
                pdfCanvas.drawBitmap(bitmap, 0f, 0f, null)

                pdfDocument.finishPage(pdfPage)

                // Clean up
                bitmap.recycle()
                page.close()
            }

            // Write compressed PDF to output file
            val outputStream = FileOutputStream(outputFile)
            pdfDocument.writeTo(outputStream)
            outputStream.close()

        } finally {
            pdfDocument.close()
            pdfRenderer.close()
            parcelFileDescriptor.close()
        }
    }

    /**
     * Alternative compression method using quality reduction
     */
    suspend fun compressPDFWithQuality(
        inputFile: File,
        outputFile: File,
        quality: Int = 60 // 60% quality for 40% compression
    ): CompressionResult = withContext(Dispatchers.IO) {

        val inputSizeMB = inputFile.length() / BYTES_IN_MB.toDouble()

        if (inputSizeMB <= MAX_SIZE_MB) {
            inputFile.copyTo(outputFile, overwrite = true)
            return@withContext CompressionResult(
                success = true,
                originalSizeMB = inputSizeMB,
                compressedSizeMB = inputSizeMB,
                message = "File size is already under ${MAX_SIZE_MB}MB."
            )
        }

        try {
            val parcelFileDescriptor = ParcelFileDescriptor.open(inputFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val pdfDocument = PdfDocument()

            try {
                for (pageIndex in 0 until pdfRenderer.pageCount) {
                    val page = pdfRenderer.openPage(pageIndex)

                    // Create bitmap with original dimensions
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    // Compress bitmap to JPEG with specified quality
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
                    val compressedBitmapData = byteArrayOutputStream.toByteArray()

                    // Create compressed bitmap from JPEG data
                    val compressedBitmap = android.graphics.BitmapFactory.decodeByteArray(
                        compressedBitmapData, 0, compressedBitmapData.size
                    )

                    // Create new page in output document
                    val pageInfo = PdfDocument.PageInfo.Builder(page.width, page.height, pageIndex + 1).create()
                    val pdfPage = pdfDocument.startPage(pageInfo)
                    val pdfCanvas = pdfPage.canvas

                    pdfCanvas.drawBitmap(compressedBitmap, 0f, 0f, null)
                    pdfDocument.finishPage(pdfPage)

                    // Clean up
                    bitmap.recycle()
                    compressedBitmap?.recycle()
                    page.close()
                    byteArrayOutputStream.close()
                }

                val outputStream = FileOutputStream(outputFile)
                pdfDocument.writeTo(outputStream)
                outputStream.close()

            } finally {
                pdfDocument.close()
                pdfRenderer.close()
                parcelFileDescriptor.close()
            }

            val compressedSizeMB = outputFile.length() / BYTES_IN_MB.toDouble()

            CompressionResult(
                success = true,
                originalSizeMB = inputSizeMB,
                compressedSizeMB = compressedSizeMB,
                message = "PDF compressed with ${quality}% quality"
            )

        } catch (e: Exception) {
            CompressionResult(
                success = false,
                originalSizeMB = inputSizeMB,
                compressedSizeMB = 0.0,
                message = "Compression failed: ${e.message}"
            )
        }
    }

    /**
     * Utility function to get file size in MB
     */
    fun getFileSizeMB(file: File): Double {
        return file.length() / BYTES_IN_MB.toDouble()
    }
}

/**
 * Data class to hold compression results
 */
data class CompressionResult(
    val success: Boolean,
    val originalSizeMB: Double,
    val compressedSizeMB: Double,
    val message: String
)