package com.app.ecarepro.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.ByteArrayOutputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import android.content.ContentResolver

import java.io.InputStream
class FileAccess {


    companion object{


        private val REQUEST_CAMERA_PERMISSION = 1001
        private val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1002

        fun checkPermission(fragment: Fragment ) {
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    fragment.requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    fragment.requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
                )
            }
        }


        fun galleryIntent( ): Intent {

          //  val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          //  fragment. startActivityForResult(intent, REQUEST_CAMERA_PERMISSION)


            return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI )

        }

          fun cameraIntent( ) : Intent {

              //  val intent = Intent()
              //intent.setType("image/*")
              // intent.setAction(Intent.ACTION_GET_CONTENT)
              //fragment.startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION)


              return Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        }



        fun bitmapFromFile(context: Context, filePath: String): Bitmap {
            return BitmapFactory.decodeFile(filePath);
        }

        fun bitmapFromUri(context: Context, imgUri: Uri?): Bitmap {
            return MediaStore.Images.Media.getBitmap(context.contentResolver, imgUri)
        }

          fun getImageExtFromUri(inContext: Context, inImage: Bitmap) : String? {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path =
                MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)

           return   getRealPathFromURI(Uri.parse(path),inContext)

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
        // Function to convert Uri to ByteArray
        fun uriToByteArray(context: Context, imgUri: Uri): ByteArray? {
            try {
                val contentResolver: ContentResolver = context.contentResolver
                val inputStream: InputStream? = contentResolver.openInputStream(imgUri)

                inputStream?.let {
                    // Decode the input stream to a bitmap
                    val bitmap = BitmapFactory.decodeStream(it)

                    // Convert bitmap to byte array
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    return byteArrayOutputStream.toByteArray()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }


        fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
            return context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }
        fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }
        fun byteArrayToBase64(byteArray: ByteArray): String {
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }
        fun uriToBase64(context: Context, uri: Uri): String? {
            val bitmap = uriToBitmap(context, uri)
            return bitmap?.let {
                val byteArray = bitmapToByteArray(it)
                byteArrayToBase64(byteArray)
            }
        }
    }

}