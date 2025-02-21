package com.app.ecarepro.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.asynctaskcoffee.audiorecorder.uikit.VoiceSenderDialog
import com.asynctaskcoffee.audiorecorder.worker.AudioRecordListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FileAccess {


    companion object {


        private val REQUEST_CAMERA_PERMISSION = 1001
        private val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1002

        fun checkPermission(fragment: Fragment) {
            if (ContextCompat.checkSelfPermission(
                    fragment.requireContext(),
                    Manifest.permission.CAMERA
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    fragment.requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
            if (ContextCompat.checkSelfPermission(
                    fragment.requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    fragment.requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
                )
            }
        }


        fun galleryIntent(): Intent {
            return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        }

        fun cameraIntent(): Intent {
            return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        }


        fun bitmapFromFile(context: Context, filePath: String): Bitmap {
            return BitmapFactory.decodeFile(filePath);
        }

        fun bitmapFromUri(context: Context, imgUri: Uri?): Bitmap {
            return MediaStore.Images.Media.getBitmap(context.contentResolver, imgUri)
        }

        fun getImageExtFromUri(inContext: Context, inImage: Bitmap): String? {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path =
                MediaStore.Images.Media.insertImage(
                    inContext.contentResolver,
                    inImage,
                    "Title",
                    null
                )

            return getRealPathFromURI(Uri.parse(path), inContext)

        }

        private fun getRealPathFromURI(uri: Uri?, inContext: Context): String? {
            val cursor: Cursor? = inContext.contentResolver.query(uri!!, null, null, null, null)
            cursor?.moveToFirst()
            val idx = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val filePath = cursor?.getString(idx!!)

            return filePath?.substring(filePath.lastIndexOf(".") + 1)
        }

        fun bitmapToByteArrayBase64String(bitmap: Bitmap): String {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        }

        fun getImageExtension(bitmap: Bitmap, compressFormat: Bitmap.CompressFormat): String {
            return when (compressFormat) {
                Bitmap.CompressFormat.JPEG -> "jpg"
                Bitmap.CompressFormat.PNG -> "png"
                Bitmap.CompressFormat.WEBP -> "webp"
                else -> "unknown"
            }
        }

        fun convertPdfToBase64(uri: Uri, inContext: Context): String {
            val inputStream = inContext.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            return bytes?.let { Base64.encodeToString(it, Base64.NO_WRAP) } ?: ""
        }


        fun writeResponseBodyToDisk(txt: String, receiptNo: String): File? {
            try {
                val dwldsPath = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "/eCarePro Download/Fee Certificate/"
                )
                if (!dwldsPath.exists()) {
                    dwldsPath.mkdirs()
                }

                val file = File.createTempFile("Certificate_$receiptNo", ".pdf", dwldsPath)
                val pdfAsBytes = Base64.decode(txt, 0)
                val os = FileOutputStream(file, false)
                os.write(pdfAsBytes)
                os.flush()
                os.close()
                return file
            } catch (e: IOException) {
                return null
            }
        }

        fun launchGallery(
            launcher: ActivityResultLauncher<Intent>,
            multiSelection: Boolean = true
        ) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiSelection)
            intent.action = Intent.ACTION_GET_CONTENT
            launcher.launch(Intent.createChooser(intent, "Select Image(s)"))
        }

        fun launchAudioPicker(
            launcher: ActivityResultLauncher<Intent>,
            multiSelection: Boolean = true
        ) {
            val intent = Intent()
            intent.type = "audio/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiSelection)
            launcher.launch(intent)
        }

        fun launchDocPicker(
            launcher: ActivityResultLauncher<Intent>,
            multiSelection: Boolean = true
        ) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*" // Allow any file type
                putExtra(
                    Intent.EXTRA_MIME_TYPES, arrayOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    )
                )
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiSelection)
            }
            launcher.launch(intent)
        }

        fun launchPdfPicker(
            launcher: ActivityResultLauncher<Intent>
        ) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            launcher.launch(intent)
        }


        fun openAudioRecorder(
            fragmentManager: FragmentManager,
            onSuccess: (uri: String?) -> Unit,
            onFailure: (errorMessage: String?) -> Unit,
        ) {
            VoiceSenderDialog(object : AudioRecordListener {
                override fun onAudioReady(audioUri: String?) {
                    onSuccess(audioUri)
                }

                override fun onReadyForRecord() {}

                override fun onRecordFailed(errorMessage: String?) {
                    onFailure(errorMessage)
                }
            }).show(fragmentManager, "VOICE")
        }

    }
}