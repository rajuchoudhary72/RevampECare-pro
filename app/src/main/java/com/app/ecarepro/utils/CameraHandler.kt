package com.app.ecarepro.utils


import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.util.UUID

class CameraHandler(
    private val fragment: Fragment,
    private val onImageCaptured: (uri: Uri?, bitmap: Bitmap?, cachedFile: File?) -> Unit
) {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private var photoURI: Uri? = null

    init {
        initializeLauncher()
    }

    private fun initializeLauncher() {
        takePictureLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    photoURI?.let { uri ->
                        Log.d(TAG, "Image capture successful. URI: $uri")
                        var bitmap: Bitmap? = null
                        var cachedFile: File? = null
                        try {
                            bitmap = if (Build.VERSION.SDK_INT < 28) {
                                MediaStore.Images.Media.getBitmap(
                                    fragment.requireContext().contentResolver,
                                    uri
                                )
                            } else {
                                val source = ImageDecoder.createSource(
                                    fragment.requireContext().contentResolver,
                                    uri
                                )
                                ImageDecoder.decodeBitmap(source)
                            }

                            // Save the bitmap to a cache file if needed by the caller
                            bitmap?.let {
                                cachedFile = File(
                                    fragment.requireContext().cacheDir,
                                    "${UUID.randomUUID()}.png"
                                )
                                cachedFile.outputStream().use { out ->
                                    it.compress(Bitmap.CompressFormat.PNG, 100, out)
                                    out.flush()
                                }
                                Log.d(
                                    TAG,
                                    "Bitmap created and saved to app cache: ${cachedFile.absolutePath}"
                                )
                            }
                            onImageCaptured(uri, bitmap, cachedFile)

                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing image: ", e)
                            onImageCaptured(
                                uri,
                                null,
                                null
                            ) // Notify with URI but null bitmap/file on error
                        }
                    } ?: run {
                        Log.e(TAG, "photoURI is null after successful capture.")
                        onImageCaptured(null, null, null)
                    }
                } else {
                    Log.d(TAG, "Image capture failed or cancelled by user.")
                    onImageCaptured(null, null, null) // Notify of failure
                }
            }
    }

    private fun createImageUri(context: Context): Uri? {
        val imageFileName = "JPEG_${System.currentTimeMillis()}_"
        val storageDir = File(context.cacheDir, "images") // Ensure this matches your file_paths.xml
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e(TAG, "Failed to create directory: $storageDir")
                return null
            }
        }

        return try {
            val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
            photoURI = FileProvider.getUriForFile(
                context,
                "${context.packageName}.myFileProvider", // Make sure this matches AndroidManifest
                imageFile
            )
            Log.d(TAG, "Image URI created: $photoURI")
            photoURI
        } catch (ex: Exception) {
            Log.e(TAG, "Error creating image URI: ", ex)
            null
        }
    }

    fun dispatchTakePictureIntent() {
        val context = fragment.context ?: run {
            Log.e(TAG, "Context is null, cannot dispatch picture intent.")
            Toast.makeText(
                fragment.activity,
                "Cannot open camera: context not available.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val uri = createImageUri(context)
        if (uri != null) {
            try {
                takePictureLauncher.launch(uri)
            } catch (e: Exception) {
                Log.e(TAG, "Error launching camera: ", e)
                Toast.makeText(context, "Could not launch camera app.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Could not prepare to take photo.", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Failed to create URI for image capture.")
        }
    }

    companion object {
        private const val TAG = "CameraHandler"
    }
}