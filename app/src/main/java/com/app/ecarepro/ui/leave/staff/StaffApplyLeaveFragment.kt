package com.app.ecarepro.ui.leave.staff

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.post_leave_request.HalfdayDTL
import com.app.ecarepro.databinding.FragmentStaffApplyLeaveBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.FileAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class StaffApplyLeaveFragment : Fragment() {


    private var days: Long = 0
    private lateinit var binding: FragmentStaffApplyLeaveBinding
    private val leaveApplyLeaveViewModel: StaffApplyLeaveViewModel by viewModels()
    private var imageExt: String = ""
    private var imageString: String = ""
    private var halfdayDTL: List<HalfdayDTL> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentStaffApplyLeaveBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val leaveID = requireArguments().getInt(Constant.LEAVE_ID_ARGUMENT)
        val leaveType = requireArguments().getString(Constant.NAME)

        binding.tvLeaveType.text = leaveType
        binding.tvStartDate.text = Constant.currentDate()
        binding.tvEndDate.text = Constant.currentDate()



        binding.llStartDate.setOnClickListener {
            ECareDataPicker(requireActivity(), true, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.tvStartDate.text = Constant.dateToShow(date.toString())
                }
            })
        }

        binding.llEndDate.setOnClickListener {
            if (binding.tvStartDate.text.toString().isNotEmpty()) {
                ECareDataPicker(
                    requireActivity(),
                    true,
                    object : ECareDataPicker.PickerCallback {
                        override fun onSelect(date: String?, isCurrentDate: Boolean) {
                            binding.tvEndDate.text = Constant.dateToShow(date.toString())

                            val diff = Constant.getLongTimeDate(binding.tvEndDate.text.toString()) -
                                    Constant.getLongTimeDate(binding.tvStartDate.text.toString())




                            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                            binding.tvDuration.text = buildString {
                                append(days + 1)
                                append(" ")
                                append(getString(R.string.day_s))
                            }
                        }
                    }).setMinDate(Constant.getLongTimeDate(binding.tvStartDate.text.toString()))


            } else Toast.makeText(requireContext(), "Select To Date", Toast.LENGTH_LONG).show()
        }


        binding.btnAttachment.setOnClickListener {
            selectImageOptionDialog()
        }
        binding.imageViewCancel.setOnClickListener {
            imageExt = ""
            imageString = ""
            binding.imageViewCancel.isVisible = false
            binding.attachmentImage.isVisible = false
        }








        binding.btnSubmit.setOnClickListener {

            if (validateData()) {
                leaveApplyLeaveViewModel.leaveApply(
                    leaveID,
                    binding.tvStartDate.text.toString(),
                    binding.tvEndDate.text.toString(),
                    (days + 1).toInt(),
                    halfdayDTL,
                    binding.textFiledReason.text.toString(),
                    imageString,
                    imageExt

                )

                lifecycleScope.launch {
                    leaveApplyLeaveViewModel.leaveApplyStateFlow.collectLatest {
                        when (it) {

                            is NetworkResult.Loading -> {
                                (requireActivity() as MainActivity).showLoader(true)
                            }

                            is NetworkResult.Error -> {
                                (requireActivity() as MainActivity).showLoader(false)
                                Log.d("main", "Error$it")
                            }

                            is NetworkResult.Success -> {
                                (requireActivity() as MainActivity).showLoader(false)
                                findNavController().popBackStack()

                            }
                        }
                    }
                }
            }

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
        builder.setItems(items) { dialog, item ->
            FileAccess.checkPermission(this@StaffApplyLeaveFragment)
            if (items[item] == getString(R.string.take_photo)) {
                cameraLauncher.launch(FileAccess.cameraIntent())
            } else if (items[item] == getString(R.string.choose_library)) {
                galleryLauncher.launch(FileAccess.galleryIntent())
            } else if (items[item] == getString(R.string.cancel)) {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                binding.attachmentImage.setImageURI(imgUri)

                val bitmap = FileAccess.bitmapFromUri(requireContext(), imgUri)

                imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

                imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()

                binding.imageViewCancel.isVisible = true
                binding.attachmentImage.isVisible = true

            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    binding.attachmentImage.setImageBitmap(bitmap)

                    imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

                    imageExt = FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()
                    binding.imageViewCancel.isVisible = true
                    binding.attachmentImage.isVisible = true

                }
            }
        }

    fun validateData(): Boolean {
        var validate = true
        if (binding.tvStartDate.text.toString().isEmpty()) {
            validate = false
            Toast.makeText(requireContext(), "Select From Date", Toast.LENGTH_LONG).show()
        }
        if (binding.tvEndDate.text.toString().isEmpty()) {
            validate = false
            Toast.makeText(requireContext(), "Select To Date", Toast.LENGTH_LONG).show()

        }

        if (binding.textFiledReason.text.toString().isEmpty()) {
            validate = false
            Toast.makeText(requireContext(), "Enter Reason", Toast.LENGTH_LONG).show()

        }
        if (!binding.cbLeaveTc.isChecked) {
            validate = false
            Toast.makeText(requireContext(), "Please Check Term and Condition", Toast.LENGTH_LONG)
                .show()

        }

        return validate
    }


}