package com.app.ecarepro.ui.leave.apply_leave

import android.app.Activity
import android.app.AlertDialog
 import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.post_leave_request.HalfdayDTL
import com.app.ecarepro.databinding.FragmentApplyLeaveBinding
import com.app.ecarepro.model.LeaveTypes
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.FileAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class  ApplyLeaveFragment : Fragment() {

    private var selectedLeaveTypeID: Int = 0
    private var leaveTypesDataString: ArrayList<String> = ArrayList()
    private lateinit var leaveTypeList: List<LeaveTypes>
    private lateinit var binding: FragmentApplyLeaveBinding
    private val leaveApplyLeaveViewModel: ApplyLeaveViewModel by viewModels()
    private lateinit var imageExt: String
    private lateinit var imageString: String
    private lateinit var halfdayDTL: List<HalfdayDTL>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding= FragmentApplyLeaveBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.llStartDate.setOnClickListener {
            ECareDataPicker(requireActivity(), true, object : ECareDataPicker.PickerCallback  {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                     binding.tvStartDate.text=date
                }

            })

            binding.llEndDate.setOnClickListener {
                if (binding.tvStartDate.text.toString().isNotEmpty()){
                    ECareDataPicker(requireActivity(), true, object : ECareDataPicker.PickerCallback  {
                        override fun onSelect(date: String?, isCurrentDate: Boolean) {
                            binding.tvEndDate.text=date
                        }
                    }).setMinDate(Constant.getLongTimeDate(binding.tvEndDate.text.toString()))


                    val diff=Constant.getLongTimeDate(binding.tvStartDate.text.toString())-Constant.getLongTimeDate(binding.tvEndDate.text.toString())

                      val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                    binding.tvNumberDays.text=days.toString()


                }  else
                    mainActivity().showMessage("Select To Date")
            }   }


        binding.btnAttachment.setOnClickListener {
            selectImageOptionDialog()
        }
        binding.imageViewCancel.setOnClickListener {
            imageExt=""
            imageString=""
            binding.imageViewCancel.isVisible=false
            binding.attachmentImage.isVisible=false
        }

        lifecycleScope.launch {
            leaveApplyLeaveViewModel.leaveSettingStateFlow.collectLatest {
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

                        if (it.data !=null) {
                            leaveTypeList=it.data.leaveTypes
                            it.data.leaveTypes.forEach { data ->
                                leaveTypesDataString.add(data.suggestion )
                            }
                            val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,leaveTypesDataString)
                            binding.autoCompleteReason.setAdapter(arrayAdapter)
                        }

                    }  }  }  }

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

                    }  }  }  }

        leaveApplyLeaveViewModel.leaveSetting()

        binding.autoCompleteReason.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedLeaveTypeID=leaveTypeList[position].lvSgID
               }

        binding.btnSubmit.setOnClickListener {

            if (validateData()){
                leaveApplyLeaveViewModel.leaveApply(
                    selectedLeaveTypeID,
                    binding.tvStartDate.text.toString(),
                    binding.tvEndDate.text.toString(),
                    binding.tvNumberDays.text.toString().toInt(),
                    halfdayDTL,
                    binding.textFiledReason.text.toString(),
                    imageString,
                    imageExt

                )
            }

        }

        binding.toggleButtonTypeLeave.addOnButtonCheckedListener { _, _, _ ->
            when (binding.toggleButtonTypeLeave.checkedButtonId) {
                R.id.btn_leave_req -> {
                    binding.llLeaveHistory.isVisible=false
                    binding.llMainLeaveRequest.isVisible=true
                } else -> {
                    binding.llLeaveHistory.isVisible=true
                    binding.llMainLeaveRequest.isVisible=false
                }
            }
        }


        lifecycleScope.launch {
            leaveApplyLeaveViewModel.leaveHistoryStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvLeaveHistory.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvLeaveHistory.isVisible = true

                        if (it.data != null) {

                            binding.rvLeaveHistory.isVisible = true

                            val leaveHistoryAdapter = StudentLeaveHistoryAdapter(
                                it.data.dtl,
                                this@ApplyLeaveFragment
                            )

                            binding.rvLeaveHistory.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(activity)
                                adapter = leaveHistoryAdapter
                            }


                        }


                    }
                }
            }

        }

        leaveApplyLeaveViewModel.leaveHistory()

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
            FileAccess.checkPermission(this@ApplyLeaveFragment)
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

                binding.imageViewCancel.isVisible=true
                binding.attachmentImage.isVisible=true

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
                    binding.imageViewCancel.isVisible=true
                    binding.attachmentImage.isVisible=true

                }
            }
        }

    private fun validateData():Boolean{
        var validate=true
        if (binding.tvStartDate.text.toString().isEmpty()){
            validate=false
            mainActivity().showMessage("Select From Date")
        }
        if (binding.tvEndDate.text.toString().isEmpty()){
            validate=false
            mainActivity().showMessage("Select To Date")

        }
        if (selectedLeaveTypeID == 0){
            validate=false
            mainActivity().showMessage("Select Leave Type")

        }
        if (binding.textFiledReason.text.toString().isEmpty()){
            validate=false
            mainActivity().showMessage("Enter Reason")

        }
        if (!binding.cbLeaveTc.isChecked){
            validate=false
            mainActivity().showMessage("Please Check Term and Condition")

        }

        return validate
    }


}