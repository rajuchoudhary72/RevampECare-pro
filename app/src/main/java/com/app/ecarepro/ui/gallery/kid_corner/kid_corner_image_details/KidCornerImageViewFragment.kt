package com.app.ecarepro.ui.gallery.kid_corner.kid_corner_image_details

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
import com.app.ecarepro.data.network.model.Video
import com.app.ecarepro.databinding.FragmentKidCornerImageViewBinding
import com.app.ecarepro.model.Photo
import com.app.ecarepro.model.photo_setting.AlbumSetting
import com.app.ecarepro.ui.gallery.photo.photo_slider.PhotoSliderFragment

@AndroidEntryPoint
class KidCornerImageViewFragment : Fragment() {

    private var _binding: FragmentKidCornerImageViewBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKidCornerImageViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photo = arguments?.getString(PHOTO)
        val createdBy = arguments?.getString(CREATED_BY)
        val name = arguments?.getString(NAME)
        val description = arguments?.getString(DESCRIPTION)
        val guideName = arguments?.getString(GUIDE_NAME)
        val classes = arguments?.getString(CLASSES)


        binding.tvCreatedBy.text = createdBy
        binding.tvName.text = name
        binding.tvDes.text = description
        binding.tvGuideName.text = guideName
        binding.tvClasses.text = classes

        photo?.let { url ->
            binding.photoView.imageUrl(url)
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
        const val CREATED_BY = "createdBy"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val GUIDE_NAME = "guideName"
        const val CLASSES = "class"

        fun newInstance(photo: String?, createdBy: String?, name: String?, description: String, guideName: String, classes: String) =
            KidCornerImageViewFragment().apply {
                arguments = Bundle().apply {
                    putString(PHOTO, photo)
                    putString(CREATED_BY, createdBy)
                    putString(NAME, name)
                    putString(DESCRIPTION, description)
                    putString(GUIDE_NAME, guideName)
                    putString(CLASSES, classes)
                }
            }
    }
}