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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentPrintOutAppointenentBinding
import com.app.ecarepro.ui.fom_guard.model.verify_code.Appdetails
import com.app.ecarepro.ui.fom_guard.verification_code.FomGuardVerfyCodeViewModel
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

 @AndroidEntryPoint
class PrintOutAppointenentFragment : Fragment() {

    private val viewModel: FomGuardVerfyCodeViewModel by viewModels()
     private lateinit var binding: FragmentPrintOutAppointenentBinding
     private var appointmentData: Appdetails? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentPrintOutAppointenentBinding.inflate(inflater, container, false)
        try {
            appointmentData = requireArguments().getParcelable<Appdetails>("appointmentData")
        } catch (_: Exception) {
        }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*viewModel.appointmentData.observe(viewLifecycleOwner, Observer {
            binding.appointmentData = it
        })*/

        binding.appointmentData = appointmentData

        binding.btnContinue.setOnClickListener {
            val bitmap = getBitmapFromView(binding.cvAppointmentDetails)
            val uri = saveImage(bitmap)
            shareImageUri(uri!!)
        }





    }

    private fun getBitmapFromView(view: View): Bitmap {
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
            uri = FileProvider.getUriForFile(requireContext(), "com.franciscan.ecare_pro.myFileProvider", file)
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