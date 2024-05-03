package com.app.ecarepro.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.ByteArrayOutputStream


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

          fun bitmapToByteArrayBase64String(bitmap: Bitmap): String {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
        }

        fun bitmapFromUri(context: Context , imgUri: Uri?): Bitmap {
            return if(Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imgUri)
            }else{
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(context.contentResolver, imgUri!!)
                ImageDecoder.decodeBitmap(source)
            }

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

    }






}