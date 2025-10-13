package com.app.ecarepro.ui.update_parent_profile

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
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
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
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
import com.app.ecarepro.ui.studentId.EscortPhoto
import com.app.ecarepro.ui.studentId.FatherPhoto
import com.app.ecarepro.ui.studentId.MotherPhoto
import com.app.ecarepro.ui.studentId.ParentPhotoRequest
import com.app.ecarepro.ui.studentId.StudentIDRequest
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.FileAccess
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@AndroidEntryPoint
class UpdateSParentProfileFragment : Fragment() {

    private lateinit var binding: FragmentUpdateStudentsProfileBinding
    private   var mMyClass= mutableListOf<MyClasseTeacherOf>()
    private val assignRollNoViewModel: UpdateParentPhotoViewModel by viewModels()
    private lateinit var selectedClassData: MyClasseTeacherOf
    private lateinit var studentData: StudentRllNo
    private var uploadImage = 0
    private var mMyClassDataString: ArrayList<String> = ArrayList()
    private var fatherImage: CircleImageView? = null
    private var motherImage: CircleImageView? = null
    private var escortImage: CircleImageView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        try {
            Picasso.setSingletonInstance(
                Picasso.Builder(requireActivity()) // additional settings
                    .build()
            )
        } catch (e: IllegalStateException) {

        }
        binding=FragmentUpdateStudentsProfileBinding.inflate(inflater,container,false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.upload_parent_photo)

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



                                val noticeAdapter = UpdateParentPhotoAdapter(
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
        studentData = student
        showParentGuardianPopup()

    }

    private fun showParentGuardianPopup() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_parent_guardian)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Initialize views
        fatherImage = dialog.findViewById<CircleImageView>(R.id.circleImageViewFather)
         motherImage = dialog.findViewById<CircleImageView>(R.id.circleImageViewMother)
         escortImage = dialog.findViewById<CircleImageView>(R.id.circleImageViewEscort)


        Picasso.get()
            .load(studentData.fatherPhoto)
            .error(R.drawable.ic_no_profile_big)
            .placeholder(R.drawable.ic_no_profile_big)
            .into(fatherImage)
        Picasso.get()
            .load(studentData.motherPhoto)
            .error(R.drawable.ic_no_profile_big)
            .placeholder(R.drawable.ic_no_profile_big)
            .into(motherImage)
        Picasso.get()
            .load(studentData.escortPhoto)
            .error(R.drawable.ic_no_profile_big)
            .placeholder(R.drawable.ic_no_profile_big)
            .into(escortImage)

        // Add click listeners if needed
        dialog.findViewById<ImageView>(R.id.iv_father_pic_edit)?.setOnClickListener {
            // Handle father image edit
            uploadImage = 1
            startImagePicker()
        }
        dialog.findViewById<ImageView>(R.id.iv_mother_pic_edit)?.setOnClickListener {
            // Handle mother image edit
            uploadImage = 2
            startImagePicker()
        }
        dialog.findViewById<ImageView>(R.id.iv_escort_pic_edit)?.setOnClickListener {
            // Handle escort image edit
            uploadImage = 3
            startImagePicker()
        }

        dialog.show()
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
                        when (uploadImage) {
                            1 -> {//f
                                fatherImage?.setImageURI(uri)
                            }
                            2 -> {//m
                                motherImage?.setImageURI(uri)
                            }
                            3 -> {//e
                                escortImage?.setImageURI(uri)
                            }
                        }
                        val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                        val imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)

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
        val requestImage = ParentPhotoRequest(studentData.stID)
        when (uploadImage) {
            1 -> {//f
                val reqFather = FatherPhoto(imageString, imageExt)
                requestImage.fatherPhoto = reqFather
            }
            2 -> {//m
                val reqMother = MotherPhoto(imageString, imageExt)
                requestImage.motherPhoto = reqMother
            }
            3 -> {//e
                val reqEscort = EscortPhoto(imageString, imageExt)
                requestImage.escortPhoto = reqEscort
            }
        }
        assignRollNoViewModel.uploadStudentPhoto(requestImage)
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

    fun getImageExtension(bitmap: Bitmap, compressFormat: Bitmap.CompressFormat): String {
        return when (compressFormat) {
            Bitmap.CompressFormat.JPEG -> "jpg"
            Bitmap.CompressFormat.PNG -> "png"
            Bitmap.CompressFormat.WEBP -> "webp"
            else -> "unknown"
        }
    }

}