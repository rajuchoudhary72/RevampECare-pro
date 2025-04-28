package com.app.ecarepro.ui.studentId


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentIdBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.FileAccess
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@AndroidEntryPoint
class StudentIDFragment : Fragment() {

    private lateinit var binding: FragmentStudentIdBinding
    private lateinit var finalFatherFile: File
    private lateinit var finalMotherFile: File
    private lateinit var finalEscortFile: File
    private lateinit var type: String
    private var uploadImage = 0

    private val mViewModel: StudentCardViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        try {
            Picasso.setSingletonInstance(
                Picasso.Builder(requireActivity()) // additional settings
                    .build()
            )
        } catch (e: IllegalStateException) {

        }
        binding = FragmentStudentIdBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.ivFatherPicEdit.setOnClickListener {
            uploadImage = 1
            startImagePicker()
        }
        binding.ivMotherPicEdit.setOnClickListener {
            uploadImage = 2
            startImagePicker()
        }
        binding.ivEscortPicEdit.setOnClickListener {
            uploadImage = 3
            startImagePicker()
            //    selectImageOptionDialog()
        }

        binding.tvPhone.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + binding.tvPhone.text.toString()))
            startActivity(intent)
        }


        getObserverData()
    }


    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1001
    private fun pickImage(type: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is already granted, start image picker
            startImagePicker()
        }

    }

    private fun selectImageOptionDialog() {
        try {
            val items = arrayOf<CharSequence>(
                "Take Photo", "Choose from Library",
                "Cancel"
            )
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Add Photo!")
            builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
                FileAccess.checkPermission(this)
                if (items[item] == "Take Photo") {
                    cameraLauncher.launch(FileAccess.cameraIntent())
                } else if (items[item] == "Choose from Library") {
                    galleryLauncher.launch(FileAccess.galleryIntent())
                } else if (items[item] == "Cancel") {
                    dialog.dismiss()
                }
            })
            builder.show()
        }catch (e:SecurityException){
            e.printStackTrace()
        }

    }

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data


                // binding.ivAddedImage.setImageURI(imgUri)
                when (uploadImage) {
                    1 -> {//f
                        binding.circleImageViewFather.setImageURI(imgUri)
                    }
                    2 -> {//m
                        binding.circleImageViewMother.setImageURI(imgUri)
                    }
                    3 -> {//e
                        binding.circleImageViewEscort.setImageURI(imgUri)
                    }
                }
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

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    /*when (uploadImage) {
                        1 -> {//f
                            binding.circleImageViewFather.setImageBitmap(bitmap)
                        }
                        2 -> {//m
                            binding.circleImageViewMother.setImageBitmap(bitmap)
                        }
                        3 -> {//e
                            binding.circleImageViewEscort.setImageBitmap(bitmap)
                        }
                    }*/

                    // binding.ivAddedImage.setImageBitmap(bitmap)

                    val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                    val imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)
                    //  val imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()

                    uploadPhoto(imageString, imageExt)

                }
            }
        }



    private fun uploadPhoto(imageString: String, imageExt: String) {
        val requestImage = StudentIDRequest()
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
        mViewModel.getPhotoUpload(requestImage)
    }

    private fun startImagePicker() {
        ImagePicker.with(this)
            .crop(216F, 253F)
            .maxResultSize(216, 253)
            // .maxResultSize(600, 800) // or higher depending on your use case
            .start()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start image picker
                startImagePicker()
            } else {
                // Permission denied, show a message to the user
                mainActivity().showMessage("Permission denied, cannot pick image")
            }
        }
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
                                binding.circleImageViewFather.setImageURI(uri)
                                finalFatherFile = File(uri.path!!)
                            }
                            2 -> {//m
                                binding.circleImageViewMother.setImageURI(uri)
                                finalMotherFile = File(uri.path!!)
                            }
                            3 -> {//e
                                binding.circleImageViewEscort.setImageURI(uri)
                                finalEscortFile = File(uri.path!!)
                            }
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



    private fun getObserverData() {

        lifecycleScope.launch {
            mViewModel._studentCardResponse.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        // binding.rvMedicineIssue.isVisible = false
                        Log.d("main", "Error$it")
                        mainActivity().showMessage(it.message?:"")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        it.data?.let { studentDTL ->
                            studentDTL.studentDTL?.let { ss ->
                                Picasso.get()
                                    .load(ss.photo)
                                    .error(R.drawable.shape_rect_trans)
                                    .placeholder(R.drawable.shape_rect_trans)
                                    .into(binding.circleImageViewProfile)
                                val blankValue = "N/A"
                                binding.tvName.text = ss.name?.ifEmpty { blankValue } ?: ""
                                binding.tvFatherName.text =
                                    ss.fatherName?.ifEmpty { blankValue } ?: ""
                                binding.tvFatherNumber.text =
                                    ss.fatherMobile?.ifEmpty { blankValue } ?: ""
                                binding.tvMotherName.text = ss.motherName?.ifEmpty { blankValue }
                                binding.tvMotherNumber.text =
                                    ss.motherMobile?.ifEmpty { blankValue } ?: ""
                                binding.tvDob.text = ss.dob?.ifEmpty { blankValue } ?: ""
                                binding.tvAddhar.text =
                                    ss.aadhaarNumber?.ifEmpty { blankValue } ?: ""
                                binding.tvPen.text =
                                    ss.peN_Number?.ifEmpty { blankValue } ?: ""
                                binding.tvClass.text =
                                    if (ss.className?.isEmpty() == true) "Class: $blankValue" else "Class: ${ss.className}"
                                binding.tvRollNo.text =
                                    if (ss.rollNo?.isEmpty() == true) "Roll No: $blankValue" else "Roll No: ${ss.rollNo}"
                                binding.tvBg.text = ss.bloodGroup?.ifEmpty { blankValue } ?: ""
                                binding.tvHouse.text = ss.house?.ifEmpty { blankValue } ?: ""
                                binding.tvAddress.text = ss.address?.ifEmpty { blankValue } ?: ""
                                binding.tvPhone.text = ss.contactmob?.ifEmpty { blankValue } ?: ""

                                if (!studentDTL.browseImgEnable!!) {
                                    binding.ivFatherPicEdit.visibility = View.GONE
                                    binding.ivMotherPicEdit.visibility = View.GONE
                                    binding.ivEscortPicEdit.visibility = View.GONE
                                }


                                Picasso.get()
                                    .load(studentDTL.fatherImgURL)
                                    .error(R.drawable.shape_rect_trans)
                                    .placeholder(R.drawable.shape_rect_trans)
                                    .into(binding.circleImageViewFather)


                                Picasso.get()
                                    .load(studentDTL.motherImgURL)
                                    .error(R.drawable.shape_rect_trans)
                                    .placeholder(R.drawable.shape_rect_trans)
                                    .into(binding.circleImageViewMother)
                                Picasso.get()
                                    .load(studentDTL.escortImgURL)
                                    .error(R.drawable.shape_rect_trans)
                                    .placeholder(R.drawable.shape_rect_trans)
                                    .into(binding.circleImageViewEscort)


                                studentDTL.apply {
                                    when (fatherReq) {
                                        "Approved" -> {
                                            binding.framLFather.setBackgroundResource(R.drawable.profile_image_circuler_bg)
                                            binding.ivFatherPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.student_approve_icon))
                                            binding.ivFatherPicEdit.isEnabled = false
                                        }
                                        "Approval pending" -> {
                                            binding.framLFather.setBackgroundResource(R.drawable.profile_image_circuler_bg_yellow)
                                            binding.ivFatherPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.studnet_pending_icon))
                                            binding.ivFatherPicEdit.isEnabled = true
                                        }
                                        "Rejected" -> {
                                            binding.framLFather.setBackgroundResource(R.drawable.profile_image_circuler_bg_red)
                                            binding.ivFatherPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.student_reject_icon))
                                            binding.ivFatherPicEdit.isEnabled = true
                                        }
                                        "Not uploded" -> {
                                            binding.ivFatherPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.edit_icon_color))
                                            binding.framLFather.setBackgroundResource(R.drawable.profile_image_circuler_bg_grry)
                                            binding.ivFatherPicEdit.isEnabled = true
                                        }
                                    }

                                    when (motherReq) {
                                        "Approved" -> {
                                            binding.framLMother.setBackgroundResource(R.drawable.profile_image_circuler_bg)
                                            binding.ivMotherPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.student_approve_icon))
                                            binding.ivMotherPicEdit.isEnabled = false
                                        }
                                        "Approval pending" -> {
                                            binding.framLMother.setBackgroundResource(R.drawable.profile_image_circuler_bg_yellow)
                                            binding.ivMotherPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.studnet_pending_icon))
                                            binding.ivMotherPicEdit.isEnabled = true
                                        }
                                        "Rejected" -> {
                                            binding.framLMother.setBackgroundResource(R.drawable.profile_image_circuler_bg_red)
                                            binding.ivMotherPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.student_reject_icon))
                                            binding.ivMotherPicEdit.isEnabled = true
                                        }
                                        "Not uploded" -> {
                                            binding.ivMotherPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.edit_icon_color))
                                            binding.framLMother.setBackgroundResource(R.drawable.profile_image_circuler_bg_grry)
                                            binding.ivMotherPicEdit.isEnabled = true
                                        }
                                    }

                                    when (escortReq) {
                                        "Approved" -> {
                                            binding.framLEscort.setBackgroundResource(R.drawable.profile_image_circuler_bg)
                                            binding.ivEscortPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.student_approve_icon))
                                            binding.ivEscortPicEdit.isEnabled = false
                                        }
                                        "Approval pending" -> {
                                            binding.framLEscort.setBackgroundResource(R.drawable.profile_image_circuler_bg_yellow)
                                            binding.ivEscortPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.studnet_pending_icon))
                                            binding.ivEscortPicEdit.isEnabled = true
                                        }
                                        "Rejected" -> {
                                            binding.framLEscort.setBackgroundResource(R.drawable.profile_image_circuler_bg_red)
                                            binding.ivEscortPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.student_reject_icon))
                                            binding.ivEscortPicEdit.isEnabled = true
                                        }
                                        "Not uploded" -> {
                                            binding.ivEscortPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.edit_icon_color))
                                            binding.framLEscort.setBackgroundResource(R.drawable.profile_image_circuler_bg_grry)
                                            binding.ivEscortPicEdit.isEnabled = true
                                        }
                                    }

                                    if (canChangeApprovedImg && browseImgEnable) {
                                        binding.ivFatherPicEdit.apply {
                                            visibility = View.VISIBLE
                                            isEnabled = true
                                            setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.edit_icon_color))
                                        }
                                        binding.ivMotherPicEdit.apply {
                                            visibility = View.VISIBLE
                                            isEnabled = true
                                            setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.edit_icon_color))
                                        }
                                        binding.ivEscortPicEdit.apply {
                                            visibility = View.VISIBLE
                                            isEnabled = true
                                            setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.edit_icon_color))
                                        }
                                    }
                                    binding.llParentStudentId.visibility = View.VISIBLE
                                }


                            }


                        }


                    }
                }
            }

        }
        lifecycleScope.launch {
            mViewModel._uploadPhotoResponse.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        // binding.rvMedicineIssue.isVisible = false
                        Log.d("main", "Error$it")
                        mainActivity().showMessage(it.message?:"")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (uploadImage == 1) {
                            binding.framLFather.setBackgroundResource(R.drawable.profile_image_circuler_bg_yellow)
                            binding.ivFatherPicEdit.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.studnet_pending_icon
                                )
                            )
                        } else if (uploadImage == 2) {
                            binding.framLMother.setBackgroundResource(R.drawable.profile_image_circuler_bg_yellow)
                            binding.ivMotherPicEdit.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.studnet_pending_icon
                                )
                            )
                        } else if (uploadImage == 3) {
                            binding.framLEscort.setBackgroundResource(R.drawable.profile_image_circuler_bg_yellow)
                            binding.ivEscortPicEdit.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.studnet_pending_icon
                                )
                            )
                        }
                        if (it.data != null) {
                            mainActivity().showMessage("${it.data.message}")
                        }
                    }
                }
            }
        }
        mViewModel.getMedicalCard()
    }
}
