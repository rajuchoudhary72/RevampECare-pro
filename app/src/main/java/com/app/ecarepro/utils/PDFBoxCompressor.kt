package com.app.ecarepro.utils


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PDFBoxCompressor {
/*    suspend fun compressPDF(inputPath: String, outputPath: String): Boolean = withContext(
        Dispatchers.IO) {
        try {
            val document = PDDocument.load(File(inputPath))

            // Method 1: Image Compression
            val pages = document.pages
            for (page in pages) {
                val resources = page.resources
                val imageNames = resources.xObjectNames

                for (imageName in imageNames) {
                    val xObject = resources.getXObject(imageName)
                    if (xObject is PDImageXObject) {
                        // Compress images within PDF
                        compressImage(xObject, resources, imageName)
                    }
                }
            }

            // Method 2: Remove unnecessary elements
            removeUnusedObjects(document)

            // Save compressed PDF
            document.save(outputPath)
            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun compressImage(image: PDImageXObject, resources: PDResources, name: COSName) {
        try {
            val bufferedImage = image.image
            val compressedImage = compressBufferedImage(bufferedImage)

            val newImage = LosslessFactory.createFromImage(resources.document, compressedImage)
            resources.put(name, newImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun compressBufferedImage(original: BufferedImage): BufferedImage {
        val scaleFactor = 0.7f // Reduce size by 30%
        val newWidth = (original.width * scaleFactor).toInt()
        val newHeight = (original.height * scaleFactor).toInt()

        val compressed = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        val g2d = compressed.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null)
        g2d.dispose()

        return compressed
    }

    private fun removeUnusedObjects(document: PDDocument) {
        // Remove unused fonts, images, and other resources
        document.catalog.pages.forEach { page ->
            // Remove duplicate resources
            val resources = page.resources
            // Implementation for removing unused resources
        }
    }*/
}