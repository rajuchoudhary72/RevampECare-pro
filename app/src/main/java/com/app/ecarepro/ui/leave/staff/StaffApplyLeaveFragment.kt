package com.app.ecarepro.ui.leave.staff

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.post_leave_request.FileAttachment
import com.app.ecarepro.data.network.model.post_leave_request.HalfdayDTL
import com.app.ecarepro.databinding.FragmentStaffApplyLeaveBinding
import com.app.ecarepro.model.LeaveTerms
import com.app.ecarepro.model.TermCondition
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.leave.leave_setting.LeaveSettingViewModel
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.Constant.Companion.toSystemDate
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.FileAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class StaffApplyLeaveFragment : Fragment() {


    private lateinit var leaveTerm: LeaveTerms
    private lateinit var termCondition: TermCondition
    private var isSessionFromSelected: Boolean=false
    private var isSessionToSelected: Boolean=false
    private var days: Double = 1.0
    private var filterDays: Double = 1.0
    private var sessionFromPos  = 0
    private var sessionToPos  = 0
    private lateinit var binding: FragmentStaffApplyLeaveBinding
    private val leaveApplyLeaveViewModel: StaffApplyLeaveViewModel by viewModels()
    private val leaveSettingViewModel : LeaveSettingViewModel by viewModels()

    private   var imageExt: String =""
    private   var imageString: String =""
    private   var halfdayDTL = mutableListOf<HalfdayDTL>()
    private val sessionList = listOf<String> ("Session 1","Session 2")
    var timestampBack: Long = 0
    var timestampforward: Long = 0

    var timestampOneDay = "86400000".toLong()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentStaffApplyLeaveBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val leaveID=  requireArguments().getInt(Constant.LEAVE_ID_ARGUMENT)
        val leaveType=  requireArguments().getString(Constant.NAME)

        binding.tvLeaveType.text=leaveType
        binding.tvStartDate.text=Constant.currentDate()
        binding.tvEndDate.text=Constant.currentDate()

        val arrayAdapter= ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, sessionList)
        binding.autoCompleteSessionTo.setAdapter(arrayAdapter)
        binding.autoCompleteSessionFrom.setAdapter(arrayAdapter)


        binding.llStartDate.setOnClickListener {

            val currentTimestamp = System.currentTimeMillis()
            timestampforward=currentTimestamp+leaveTerm.forwardDays * timestampOneDay
            timestampBack=currentTimestamp-leaveTerm.backwardDays * timestampOneDay

            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.tvStartDate.text = Constant.dateToShow(date.toString())
                }
            },timestampBack,timestampforward)
        }



        binding.llEndDate.setOnClickListener {
            if (binding.tvStartDate.text.toString().isNotEmpty()) {

                val timestampforward=Constant.getLongTimeDate(binding.tvStartDate.text.toString())+timestampOneDay*leaveTerm.daysLimit
                val timestampBack=Constant.getLongTimeDate(binding.tvStartDate.text.toString())
                ECareDataPicker(
                    requireActivity(),
                    false,
                    object : ECareDataPicker.PickerCallback {
                        override fun onSelect(date: String?, isCurrentDate: Boolean) {
                            binding.tvEndDate.text = Constant.dateToShow(date.toString())

                            val diff = Constant.getLongTimeDate(binding.tvEndDate.text.toString()) -
                                    Constant.getLongTimeDate(binding.tvStartDate.text.toString())
                            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toDouble()+1

                            binding.tvDuration.text = buildString {
                                append(days )
                                append(" ")
                                append(getString(R.string.day_s))
                            }
                        }
                    },timestampBack,timestampforward)


            } else
                mainActivity().showMessage("Select To Date")
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

                if (sessionFromPos==0 && sessionToPos==1){
                    halfdayDTL.clear()
                } else
                    if (sessionFromPos==0 && sessionToPos==0){
                        halfdayDTL.add(HalfdayDTL(
                             Constant.toSystemDate(binding.tvEndDate.text.toString()),
                            1
                        ))
                        days -= 0.5
                    }else
                        if (sessionFromPos==1 && sessionToPos==0){
                            halfdayDTL.add(HalfdayDTL(
                                Constant.toSystemDate(binding.tvStartDate.text.toString()),
                                2
                            ))
                            halfdayDTL.add(HalfdayDTL(
                                Constant.toSystemDate(binding.tvEndDate.text.toString()),
                                1
                            ))
                            days -= 1
                        }else
                            if (sessionFromPos==1 && sessionToPos==1){
                                halfdayDTL.add(HalfdayDTL(
                                    Constant.toSystemDate(binding.tvStartDate.text.toString()),
                                    2
                                ))
                                days -= 0.5
                            }

                binding.tvDuration.text = buildString {
                    append(days )
                    append(" ")
                    append(getString(R.string.day_s))
                }


                leaveApplyLeaveViewModel.leaveApply(
                    leaveID,
                    toSystemDate(binding.tvStartDate.text.toString()),
                    toSystemDate(binding.tvEndDate.text.toString()),
                    days ,
                    halfdayDTL,
                    binding.textFiledReason.text.toString(),
                    if (imageString.isNotEmpty()) FileAttachment(imageString, imageExt, "") else null
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
                                mainActivity().showMessage(it.message.toString())
                                (requireActivity() as MainActivity).showLoader(false)
                                findNavController().popBackStack()

                            }
                        }
                    }
                }
            }

        }


        binding.autoCompleteSessionFrom.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                isSessionFromSelected=true
                sessionFromPos=position

 
              }
        binding.autoCompleteSessionTo.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                isSessionToSelected=true
               sessionToPos=position
              }

        getTermDetails()

        binding.tvTc.setOnClickListener {
            i_agree_dialog()
        }



    }


    private fun i_agree_dialog() {
        val tv_tc: TextView
        val tv_rfl: TextView
        val tv_imp_notes: TextView
        val btn_agree: Button
        val iv_cancel: ImageView
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (null != dialog.window) dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Animations
        dialog.setContentView(R.layout.dialog_leave_term_conditions)
        iv_cancel = dialog.findViewById<ImageView>(R.id.iv_cancel)
        tv_tc = dialog.findViewById(R.id.tv_tc)
        tv_rfl = dialog.findViewById<TextView>(R.id.tv_rfl)
        tv_imp_notes = dialog.findViewById<TextView>(R.id.tv_imp_notes)
        tv_tc.text = if (TextUtils.isEmpty(
                termCondition.tc
            )
        ) "" else termCondition.tc
        tv_rfl.text = if (TextUtils.isEmpty(
                termCondition.rules
            )
        ) "" else termCondition.rules
        tv_imp_notes.text = if (TextUtils.isEmpty(
                termCondition.notes
            )
        ) "" else termCondition.notes
        btn_agree = dialog.findViewById<Button>(R.id.btn_agree)
        btn_agree.visibility = View.GONE
        btn_agree.setOnClickListener { }
        iv_cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun getTermDetails() {

        lifecycleScope.launch {
            leaveSettingViewModel.leaveSettingStateFlow.collectLatest {
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
                            leaveTerm=it.data.leaveTerms
                            termCondition=it.data.termCondition

                        }

                    }  }  }  }
        leaveSettingViewModel.leaveSetting()
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

    private fun validateData(): Boolean {
        var validate = true
        if (binding.tvStartDate.text.toString().isEmpty()) {
            validate = false
            mainActivity().showMessage("Select From Date")
        }
        if (binding.tvEndDate.text.toString().isEmpty()) {
            validate = false
            mainActivity().showMessage("Select To Date")

        }

        if (binding.textFiledReason.text.toString().isEmpty()) {
            validate = false
            mainActivity().showMessage("Enter Reason")
        }
        if (!binding.cbLeaveTc.isChecked) {
            validate = false
            mainActivity().showMessage("Please Check Term and Condition")

        }
        /*if (imageString==""){
            validate = false

            mainActivity().showMessage("Attachment is mandatory")
        }*/
        if (!isSessionFromSelected){
            validate = false
            mainActivity().showMessage("Select From session ")
        }
        if (!isSessionToSelected){
            validate = false
            mainActivity().showMessage("Select To session ")
        }

        return validate
    }


}