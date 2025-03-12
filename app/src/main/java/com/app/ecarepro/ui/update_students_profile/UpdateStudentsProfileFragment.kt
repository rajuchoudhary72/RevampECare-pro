package com.app.ecarepro.ui.update_students_profile

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        selectImageOptionDialog()
        studentData=student
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
                    mainActivity().showMessage(getString(R.string.photo_uploaded_successfully))
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