package com.app.ecarepro.ui.assignment.submit_assignment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentSubmitAssignmentBinding
import com.app.ecarepro.model.Assignment
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.FileAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable


@AndroidEntryPoint
class SubmitAssignmentFragment : Fragment() {

    private lateinit var assignmentDetails: Assignment
    private lateinit var binding : FragmentSubmitAssignmentBinding
    private val submitAssignmentViewModel : SubmitAssignmentViewModel  by viewModels( )
    private   var imageExt: String= ""
    private   var imageString: String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding = FragmentSubmitAssignmentBinding.inflate(inflater,container,false)
        arguments?.getParcelable<Assignment>(Constant.ASSIGNMENT_ID).let { data ->
            assignmentDetails= data!!
            binding.assData=assignmentDetails

        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postAnswer.setOnClickListener {

            if (binding.etAnswer.text.isNotEmpty()){
                lifecycleScope.launch {
                    submitAssignmentViewModel.submitAssignment(
                        id= assignmentDetails.id.toString(),
                        asgID = assignmentDetails.asgID!!,
                        data = binding.etAnswer.text.toString(),
                        attachment = "",
                        fileName = imageString,
                        fileURL = "",
                        fileExt = imageExt

                    ).invokeOnCompletion {
                        mainActivity().showMessage("Submitted Successfully!!!  " )
                        findNavController().popBackStack()
                    }
                }
            }
        }


        binding.etAnswer.doAfterTextChanged {
            if (it != null) {
                if (it.isNotEmpty()){
                    binding.postAnswer.isEnabled = true
                    binding.postAnswer.setImageResource(R.drawable.send_icon_enable)

                    

                }else{
                    binding.postAnswer.isEnabled = false
                    binding.postAnswer.setImageResource(R.drawable.send_icon_light)
                }

            }
        }

        binding.llFile.setOnClickListener {
            binding.llFile.isVisible=false
            imageString=""
            imageExt=""

        }

        binding.tvBrowsePhoto.setOnClickListener {
            selectImageOptionDialog()
        }

    }


    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.choose_library),
            getString(R.string.cancel)

        )
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.add_photo))
        builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
            FileAccess.checkPermission(this@SubmitAssignmentFragment)
            if (items[item] == getString(R.string.take_photo)) {
                cameraLauncher.launch(FileAccess.cameraIntent())
            } else if (items[item] == getString(R.string.choose_library)) {
                galleryLauncher.launch(FileAccess.galleryIntent())
            } else if (items[item] == getString(R.string.cancel)) {
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


                    imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

                    imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()


                }
            }
        }

}