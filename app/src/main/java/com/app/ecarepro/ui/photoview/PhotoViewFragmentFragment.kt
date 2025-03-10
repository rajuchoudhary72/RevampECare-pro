package com.app.ecarepro.ui.photoview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.databinding.FragmentPhotoViewBinding
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.imageUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.UUID
import com.app.ecarepro.ui.mainActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.content.FileProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build

@AndroidEntryPoint
class PhotoViewFragmentFragment : Fragment() {

    private var _binding: FragmentPhotoViewBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photo = arguments?.getString(PHOTO)

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        photo?.let { url ->
            binding.photoView.imageUrl(url)
        }

        binding.btnDownload.setOnClickListener {
            arguments?.getString(PHOTO)?.let { photo ->
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(photo, "Photo", "image/jpeg")
                Toast.makeText(requireContext(), "Downloading started", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnShare.setOnClickListener {
            if (photo.isNullOrEmpty()) return@setOnClickListener
            viewLifecycleOwner.lifecycleScope.launch {
                mainActivity().showLoader(true)
                val file = downloadImageAndCache(requireContext(), photo)
                mainActivity().showLoader(false)
                if (file != null) {
                    shareImageFile(requireContext(), file)
                } else {
                    mainActivity().showMessage("Something went wrong, please try again")
                }
            }
        }
    }
    private fun shareImageFile(context: Context, imageFile: File) {
        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                requireContext(),
                "${context.packageName}.myFileProvider",
                imageFile
            )
        } else {
            Uri.fromFile(imageFile)
        }
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share image via"))
    }
    private suspend fun downloadImageAndCache(context: Context, imageUrl: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val cacheDir = context.cacheDir
                val imageFile = File(cacheDir, "image_${UUID.randomUUID()}.jpg")
                val outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                imageFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PHOTO = "photo"
    }
}