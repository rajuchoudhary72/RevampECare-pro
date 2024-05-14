package com.app.ecarepro.ui.studentId


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentIdBinding
import com.app.ecarepro.databinding.StudentMedicalCardBinding
import com.app.ecarepro.ui.MainActivity
import com.github.dhaval2404.imagepicker.ImagePicker

import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
@AndroidEntryPoint
class StudentIDFragment : Fragment() {

    private lateinit var binding: FragmentStudentIdBinding
    private lateinit var finalFatherFile: File
    private lateinit var finalMotherFile: File
    private lateinit var finalEscortFile: File
    private lateinit var type: String

    private val mViewModel: StudentCardViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            Picasso.setSingletonInstance(
                Picasso.Builder(requireActivity()) // additional settings
                    .build()
            )
        }catch (e:IllegalStateException){

        }
        binding = FragmentStudentIdBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {findNavController().popBackStack() }

        binding.ivFatherPicEdit.setOnClickListener { pickImage("1") }
        binding.ivMotherPicEdit.setOnClickListener { pickImage("2") }
        binding.ivEscortPicEdit.setOnClickListener { pickImage("3") }

        binding.tvPhone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + binding.tvPhone.text.toString()))
            startActivity(intent)
        }


       getObserverData()
    }


    private  val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1001
    private fun pickImage(type: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
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

    private fun startImagePicker() {
        ImagePicker.with(requireActivity())
            .crop(216F, 253F)
            .compress(4096)
            .maxResultSize(216, 253)
            .start()
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start image picker
                startImagePicker()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(
                    requireContext(),
                    "Permission denied, cannot pick image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let { uri ->
                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
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

                        when (type) {
                            "1" -> {
                                binding.circleImageViewFather.setImageURI(uri)
                                finalFatherFile = File(uri.path!!)
                            }
                            "2" -> {
                                binding.circleImageViewMother.setImageURI(uri)
                                finalMotherFile = File(uri.path!!)
                            }
                            "3" -> {
                                binding.circleImageViewEscort.setImageURI(uri)
                                finalEscortFile = File(uri.path!!)
                            }
                        }

                        hitUploadProfileApi()
                    } else {
                        Toast.makeText(context, "Oops...!!! could not proceed, the image height must be greater than 252 pixels.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun hitUploadProfileApi() {
       /* showLoadingDialog()
        val finalFile: File?
        var imageName = ""
        val url = "api/Upload/IDCardImg?SchCode=$SCHOOL_CODE_APP_USER&UserID=$USER_ID_APP_USER&key=$Constant.key"
        when (type) {
            "1" -> {
                finalFile = finalFatherFile
                imageName = "Father"
            }
            "2" -> {
                finalFile = finalMotherFile
                imageName = "Mother"
            }
            "3" -> {
                imageName = "Escort"
                finalFile = finalEscortFile
            }
        }

        val body = MultipartBody.Part.createFormData("picture", "$imageName.jpeg", RequestBody.create(MediaType.parse("image/JPEG"), finalFile))
        val interfaceRetrofit = RetrofitAdapter.createService(ApiInterface::class.java)
        val responseCall = interfaceRetrofit.UploadProfilePic(url, body)
        responseCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                hideLoadingDialog()
                if (response.code() == 201) {
                    when (type) {
                        "1" -> {
                            binding.framLFather.setBackgroundResource(R.drawable.profile_image_circuler_bg_yellow)
                            binding.ivFatherPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.studnet_pending_icon))
                            Snackbar.make(binding.scParentLayout, "It will be uploaded after verification", Snackbar.LENGTH_LONG).show()
                        }
                        "2" -> {
                            binding.framLMother.setBackgroundResource(R.drawable.profile_image_circuler_bg_yellow)
                            binding.ivMotherPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.studnet_pending_icon))
                            Snackbar.make(binding.scParentLayout, "It will be uploaded after verification", Snackbar.LENGTH_LONG).show()
                        }
                        "3" -> {
                            binding.framLEscort.setBackgroundResource(R.drawable.profile_image_circuler_bg_yellow)
                            binding.ivEscortPicEdit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.studnet_pending_icon))
                            Snackbar.make(binding.scParentLayout, "It will be uploaded after verification", Snackbar.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Snackbar.make(binding.scParentLayout, Constant.server_error, Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                hideLoadingDialog()
                Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
            }
        })*/
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
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        it.data?.let {studentDTL->
                            studentDTL.studentDTL?.let {ss->
                                Picasso.get()
                                    .load(ss.photo)
                                    .error(R.drawable.shape_rect_trans)
                                    .placeholder(R.drawable.shape_rect_trans)
                                    .into(binding.circleImageViewProfile)
                                val blankValue = "N/A"
                                binding.tvName.text = ss.name?.ifEmpty { blankValue } ?: ""
                                binding.tvFatherName.text = ss.fatherName?.ifEmpty { blankValue } ?: ""
                                binding.tvFatherNumber.text =
                                    ss.fatherMobile?.ifEmpty { blankValue } ?: ""
                                binding.tvMotherName.text = ss.motherName?.ifEmpty { blankValue }
                                binding.tvMotherNumber.text =
                                    ss.motherMobile?.ifEmpty { blankValue } ?: ""
                                binding.tvDob.text = ss.dob?.ifEmpty { blankValue } ?: ""
                                binding.tvAddhar.text = ss.aadhaarNumber?.ifEmpty { blankValue } ?: ""
                                binding.tvClass.text = if (ss.className?.isEmpty() == true) "Class: $blankValue" else "Class: ${ss.className}"
                                binding.tvRollNo.text = if (ss.rollNo?.isEmpty() == true) "Roll No: $blankValue" else "Roll No: ${ss.rollNo}"
                                binding.tvBg.text = ss.bloodGroup?.ifEmpty { blankValue } ?: ""
                                binding.tvHouse.text = ss.house?.ifEmpty { blankValue } ?: ""
                                binding.tvAddress.text = ss.address?.ifEmpty { blankValue } ?: ""
                                binding.tvPhone.text = ss.contactmob?.ifEmpty { blankValue } ?: ""

                                if (!studentDTL.browseImgEnable!!) {
                                    binding.ivFatherPicEdit.visibility = View.GONE
                                    binding.ivMotherPicEdit.visibility = View.GONE
                                    binding.ivEscortPicEdit.visibility = View.GONE
                                }

                                binding.ivFatherPicEdit.isEnabled = studentDTL.canChangeApprovedImg == true && studentDTL.browseImgEnable
                                binding.ivMotherPicEdit.isEnabled = studentDTL.canChangeApprovedImg == true && studentDTL.browseImgEnable
                                binding.ivEscortPicEdit.isEnabled = studentDTL.canChangeApprovedImg == true && studentDTL.browseImgEnable

                                binding.llParentStudentId.visibility = View.VISIBLE
                            }




                        }




                    }
                }
            }

        }
        mViewModel.getMedicalCard()
    }



    private fun showLoadingDialog() {
        // Implement your loading dialog logic here
    }

    private fun hideLoadingDialog() {
        // Implement your hiding loading dialog logic here
    }

    private fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
