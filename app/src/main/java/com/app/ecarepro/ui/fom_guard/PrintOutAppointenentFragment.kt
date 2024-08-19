package com.app.ecarepro.ui.fom_guard

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.app.ecarepro.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PrintOutAppointenentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         return inflater.inflate(R.layout.fragment_print_out_appointenent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bitmap = getBitmapFromView(view)
        val uri = saveImage(bitmap)
        shareImageUri(uri!!)



    }

    fun getBitmapFromView(view: View): Bitmap {
         val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
         val canvas = Canvas(returnedBitmap)
         val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
         view.draw(canvas)
         return returnedBitmap
    }

    private fun saveImage(image: Bitmap): Uri? {
         val imagesFolder: File = File(requireActivity().cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file: File = File(imagesFolder, "shared_image.png")

            val stream: FileOutputStream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(requireContext(), "com.mydomain.fileprovider", file)
        } catch (e: IOException) {
            //Log.d(TAG, "IOException while trying to write file for sharing: " + e.message)
        }
        return uri
    }

    private fun shareImageUri(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setType("image/png")
        startActivity(intent)
    }

}