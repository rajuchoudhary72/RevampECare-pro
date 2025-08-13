package com.app.ecarepro.ui.update_students_profile

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.StudentPhotoUploadModel
import com.app.ecarepro.databinding.FragmentUpdateStudentsProfileBinding
import com.app.ecarepro.model.MyClasseTeacherOf
import com.app.ecarepro.model.StudentRllNo
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.birthday.BirthListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.FileAccess
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@AndroidEntryPoint
class UpdateStudentsProfileFragment : Fragment() {

    private lateinit var binding: FragmentUpdateStudentsProfileBinding
    private   var mMyClass= mutableListOf<MyClasseTeacherOf>()
    private val assignRollNoViewModel: UpdateStudentPhotoViewModel by viewModels()
    private lateinit var selectedClassData: MyClasseTeacherOf
    private lateinit var studentData: StudentRllNo

    private var mMyClassDataString: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentUpdateStudentsProfileBinding.inflate(inflater,container,false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.upload_student_photo)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.autoCompleteClass.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                selectedClassData = mMyClass[pos]
                assignRollNoViewModel.getStudentListToAssignRollNo(selectedClassData.id, Constant.FILTER_NAME)

            }
        getMyClass()
    }

    fun getMyClass() {
        lifecycleScope.launch {
            assignRollNoViewModel.classTeacherOfStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {
                            if (it.data.myClasses != null) {

                                mMyClass = it.data.myClasses.toMutableList()

                                mMyClass.forEach { data ->
                                    mMyClassDataString.add(data.className.toString())
                                }


                                if (mMyClass != null && mMyClass.isNotEmpty()) {
                                    selectedClassData = mMyClass[0]
                                    binding.autoCompleteClass.setText(
                                        selectedClassData.className,
                                        false
                                    )
                                    if (selectedClassData != null) {
                                        getStudentListToAssignRollNo()
                                    }
                                }


                                val arrayAdapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_list_item_1,
                                    mMyClassDataString
                                )
                                binding.autoCompleteClass.setAdapter(arrayAdapter)
                            }
                        }


                    }


                }
            }
        }
        assignRollNoViewModel.getClassTeacherOf()
    }

    private fun getStudentListToAssignRollNo() {
        lifecycleScope.launch {
            assignRollNoViewModel.assignRollNoStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvStudentsList.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvStudentsList.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvStudentsList.isVisible = true

                        if (it.data != null) {

                            if (it.data.students != null) {

                                binding.rvStudentsList.isVisible = true
                                binding.tvNoData.isVisible = false



                                val noticeAdapter = UpdateStudentPhotoAdapter(
                                    it.data.students.toMutableList() ){ student ->
                                    uploadStudentPhoto(student)
                                }

                                binding.rvStudentsList.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = noticeAdapter
                                }

                            } else {
                                binding.rvStudentsList.isVisible = false
                                binding.tvNoData.isVisible = true
                            }

                        }

                    }

                    else -> {}
                }
            }
        }
        assignRollNoViewModel.getStudentListToAssignRollNo(selectedClassData.id, Constant.FILTER_NAME)
    }

    private fun uploadStudentPhoto(student: StudentRllNo) {
        // selectImageOptionDialog()
        startImagePicker()
        studentData = student
    }


    private fun startImagePicker() {
        FileAccess.checkPermission(this)
        ImagePicker.with(this)
            .crop(216F, 253F)
            .maxResultSize(216, 253)
            // .maxResultSize(600, 800) // or higher depending on your use case
            .start()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let { uri ->
                try {
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                    val h = bitmap.height
                    val w = bitmap.width

                    if (h >= 253 && w >= 216) {
                        val baos = ByteArrayOutputStream()
                        var fis: FileInputStream? = null
                        try {
                            fis = FileInputStream(File(uri.path))
                            val buf = ByteArray(1024)
                            var n: Int
                            while (fis.read(buf).also { n = it } != -1) {
                                baos.write(buf, 0, n)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                        val imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)
                        //  val imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()

                        uploadPhoto(imageString, imageExt)

                    } else {
                        mainActivity().showMessage("Oops...!!! could not proceed, the image height must be greater than 252 pixels.")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
 /*   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let { uri ->
                try {
                    // Get original bitmap
                    val originalBitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

                    // Fix rotation if needed
                    val rotatedBitmap = fixImageRotation(uri, originalBitmap)

                    val originalHeight = rotatedBitmap.height
                    val originalWidth = rotatedBitmap.width
                    Log.d("before ", "originalHeight $originalHeight")
                    Log.d("before ", "originalWidth $originalWidth")
                    // Create a scaled bitmap based on size requirements
                    val scaledBitmap = when {
                        // If image is smaller than 216×253, keep original size
                        originalWidth <= 216 && originalHeight <= 253 -> {
                            rotatedBitmap // No change needed
                        }
                        // If image is between 216×253 and 432×506, scale to 216×253
                        (originalWidth > 216 || originalHeight > 253) &&
                                (originalWidth < 432 || originalHeight < 506) -> {
                            Bitmap.createScaledBitmap(rotatedBitmap, 216, 253, true)
                        }
                        // If image is larger than or equal to 432×506, scale to 432×506
                        else -> {
                            Bitmap.createScaledBitmap(rotatedBitmap, 432, 506, true)
                        }
                    }

                    // Convert the scaled bitmap to Base64 string
                    val imageString = FileAccess.bitmapToByteArrayBase64String(scaledBitmap)
                    val imageExt = getImageExtension(scaledBitmap, Bitmap.CompressFormat.JPEG)

                    // Upload the processed image
                    uploadPhoto(imageString, imageExt)

                    // Recycle bitmaps to free memory (only if we created a new bitmap)
                    if (originalBitmap != rotatedBitmap) {
                        originalBitmap.recycle()
                    }
                    if (rotatedBitmap != scaledBitmap) {
                        rotatedBitmap.recycle()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    mainActivity().showMessage("Error processing image: ${e.message}")
                }
            }
        }
    }

    // Function to fix image rotation based on EXIF data
    private fun fixImageRotation(uri: Uri, bitmap: Bitmap): Bitmap {
        var rotatedBitmap = bitmap
        try {
            // Get orientation from EXIF data
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val exif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ExifInterface(inputStream!!)
            } else {
                val filePath = getRealPathFromURI(uri)
                if (filePath != null) {
                    ExifInterface(filePath)
                } else {
                    return bitmap // Can't get EXIF data, return original
                }
            }

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            // Rotate bitmap if needed
            rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                else -> bitmap
            }

            inputStream?.close()

        } catch (e: Exception) {
            e.printStackTrace()
            // If anything goes wrong, return the original bitmap
            return bitmap
        }

        return rotatedBitmap
    }

    // Helper function to rotate image
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    // Helper function to get real file path from URI
    private fun getRealPathFromURI(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            it.moveToFirst()
            val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            if (idx >= 0) it.getString(idx) else null
        }
    }*/


    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.choose_from_library),
            getString(R.string.cancel),
        )
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.add_photo))
        builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
            FileAccess.checkPermission(this)
            if (items[item] == getString(R.string.take_photo)) {
                cameraLauncher.launch(FileAccess.cameraIntent())
            } else if (items[item] == getString(R.string.choose_from_library)) {
                galleryLauncher.launch(FileAccess.galleryIntent())
            } else if (items[item] == getString(R.string.cancel)) {
                dialog.dismiss()
            }
        })
        builder.show()
    }


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap

                    val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                    val imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)

                    uploadPhoto(imageString, imageExt)

                }
            }
        }

    private fun uploadPhoto(imageString: String, imageExt: String) {

        lifecycleScope.launch {
            assignRollNoViewModel.uploadStudentPhotoStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    } is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    Log.d("main", "Error$it")
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)
                    mainActivity().showMessage("Photo uploaded successfully")
                    assignRollNoViewModel.getStudentListToAssignRollNo(selectedClassData.id, Constant.FILTER_NAME)
                } else -> {}
                }
            }
        }
        assignRollNoViewModel.uploadStudentPhoto(StudentPhotoUploadModel(imageString,imageExt,studentData.stID) )
    }


    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data

                val bitmap = FileAccess.bitmapFromUri(requireContext(), imgUri)

                val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                val imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)
                uploadPhoto(imageString, imageExt)

            }
        }

    private fun getImageExtension(bitmap: Bitmap, compressFormat: Bitmap.CompressFormat): String {
        return when (compressFormat) {
            Bitmap.CompressFormat.JPEG -> "jpg"
            Bitmap.CompressFormat.PNG -> "png"
            Bitmap.CompressFormat.WEBP -> "webp"
            else -> "unknown"
        }
    }

}