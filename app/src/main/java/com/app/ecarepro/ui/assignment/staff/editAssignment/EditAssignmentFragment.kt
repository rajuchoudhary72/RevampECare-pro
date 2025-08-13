package com.app.ecarepro.ui.assignment.staff.editAssignment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkViewAssignment

import com.app.ecarepro.databinding.FragmentEditAssignmentBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.FileAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditAssignmentFragment : Fragment() {

    private var viewAssignmentData: NetworkViewAssignment? = null
    private lateinit var binding :  FragmentEditAssignmentBinding
    private val editAssignmentViewModel : EditAssignmentViewModel by viewModels()
    private var assignmentId: String  = ""
    private   var imageExt: String= ""
    private   var imageString: String=""
    private var isFileRemoved: Boolean=true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding= FragmentEditAssignmentBinding.inflate(inflater,container,false)
        assignmentId = requireArguments().getString(Constant.ASSIGNMENT_ID).toString()
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            editAssignmentViewModel.viewAssignmentStateFlow.collectLatest {
                when (it) {  is NetworkResult.Loading -> {
                    (requireActivity() as MainActivity).showLoader(true)
                }  is NetworkResult.Error -> {
                    (requireActivity() as MainActivity).showLoader(false)
                } is NetworkResult.Success -> {
                    (requireActivity() as MainActivity).showLoader(false)

                    val  data= it.data
                    viewAssignmentData= it.data

                    if (data!=null){
                        binding.tvSubject.text= ""
                        binding.etTitle.setText(data.title)
                        binding.etDescription.setText(data.data)
                        binding.ctvAssignmentDt.text= data.asgDate
                        binding.tvSubmissionDt.text= data.submitDate
                    }
                }  }
            } }

        editAssignmentViewModel.viewAssignment(assignmentId)

        binding.btnSubmit.setOnClickListener {
            uploadAssignment()
        }

        binding.tvAddAttac.setOnClickListener {
            selectImageOptionDialog()
        }

        binding.llFile.setOnClickListener {
            binding.llFile.isVisible=false
            imageString=""
            imageExt=""
            isFileRemoved=true
        }

    }

    private fun uploadAssignment() {

        var isValidate= true


        if ( binding.etTitle.text.toString().isEmpty()){
            isValidate=false
            mainActivity().showMessage(getString(R.string.general_enter_title))
        }
        if ( binding.ctvAssignmentDt.text.toString()==getString(R.string.assignment_assignment_date)){
            isValidate=false
            mainActivity().showMessage(getString(R.string.assignment_select_assignment_date))
        }
        if (viewAssignmentData!!.submitDate!=""){
            if ( binding.tvSubmissionDt.text.toString()==getString(R.string.assignment_submission_date)){
                isValidate=false
                mainActivity().showMessage(getString(R.string.assignment_select_submission_date))
            }
        }
        if ( binding.etDescription.text.toString().isEmpty()){
            isValidate=false
            mainActivity().showMessage(getString(R.string.general_enter_date))
        }




        if (isValidate ){
            var submitDate=""
            submitDate = if (viewAssignmentData!!.submitDate!=""){
                binding.tvSubmissionDt.text.toString()
            }else{
                ""
            }


                editAssignmentViewModel.createAssignment(
                    binding.ctvAssignmentDt.text.toString(),
                    viewAssignmentData!!.asgID   ,
                    imageString,
                    imageExt,
                    "",
                    viewAssignmentData!!.classID,
                    viewAssignmentData!!.classID.toString(),
                    binding.etDescription.text.toString() ,
                    viewAssignmentData!!.id,
                    viewAssignmentData!!.id,
                    binding.cbActive.isChecked,
                    false,
                    binding.cbMultipleActive.isChecked,
                    viewAssignmentData!!.subjectID,
                    submitDate,
                    binding.etTitle.text.toString()  )


            lifecycleScope.launch {
                editAssignmentViewModel.createAssignmentStateFlow.collectLatest {
                    when (it) {  is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }  is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    } is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        mainActivity().showMessage(getString(R.string.assignment_updated_successfully))
                        findNavController().popBackStack()
                    }  }
                } }
        }


    }

    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            getString(R.string.general_take_photo),
            getString(R.string.general_choose_library),
            getString(R.string.general_cancel)

        )
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.general_add_photo))
        builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
            FileAccess.checkPermission(this@EditAssignmentFragment)
            if (items[item] == getString(R.string.general_take_photo)) {
                cameraLauncher.launch(FileAccess.cameraIntent())
            } else if (items[item] == getString(R.string.general_choose_library)) {
                galleryLauncher.launch(FileAccess.galleryIntent())
            } else if (items[item] == getString(R.string.general_cancel)) {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                binding.llFile.isVisible=true
                isFileRemoved=false

                val bitmap = FileAccess.bitmapFromUri(requireContext(), imgUri)

                imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

                imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()

            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    binding.llFile.isVisible=true
                    isFileRemoved=false

                    imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

                    imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()


                }
            }
        }
}