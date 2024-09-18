package com.app.ecarepro.ui.leave.apply_leave

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
import com.app.ecarepro.databinding.FragmentApplyLeaveBinding
import com.app.ecarepro.model.Holiday
import com.app.ecarepro.model.LeaveTerms
import com.app.ecarepro.model.LeaveTypes
import com.app.ecarepro.model.TermCondition
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.Constant.Companion.holidayLastDateGreaterSelectLastDate
import com.app.ecarepro.utils.Constant.Companion.isDateInBetweenIncludingEndPoints
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.FileAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ApplyLeaveFragment : Fragment() {

    private var isAttachamentMandetoy: Boolean=false
    private lateinit var termCondition: TermCondition
    private lateinit var leaveTerm: LeaveTerms
    private var selectedLeaveTypeID: Int = 0
    private var leaveTypesDataString: ArrayList<String> = ArrayList()
    private lateinit var leaveTypeList: List<LeaveTypes>
    private lateinit var binding: FragmentApplyLeaveBinding
    private val leaveApplyLeaveViewModel: ApplyLeaveViewModel by viewModels()
    private var imageExt = ""
    private var imageString = ""
    private var days: Double = 0.0
    private var holidayList = mutableListOf<Holiday>()

    private var halfdayDTL = mutableListOf<HalfdayDTL>()
    var timestampBack: Long = 0
    var timestampforward: Long = 0

    var timestampOneDay = "86400000".toLong()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentApplyLeaveBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvStartDate.text=Constant.currentDate()
        binding.tvEndDate.text=Constant.currentDate()


        binding.llStartDate.setOnClickListener {

            val currentTimestamp = System.currentTimeMillis()
            timestampforward=currentTimestamp+leaveTerm.forwardDays * timestampOneDay
            if (leaveTerm.isPrevDatesAllow){
                timestampBack=currentTimestamp-leaveTerm.backwardDays * timestampOneDay
            }

            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.tvStartDate.text = Constant.dateToShow(date.toString())
                }
            },timestampBack,timestampforward)
        }

        binding.llEndDate.setOnClickListener {
            if (binding.tvStartDate.text.toString().isNotEmpty()) {

                val timestampforward=Constant.getLongTimeDate(binding.tvStartDate.text.toString())+timestampOneDay*(leaveTerm.daysLimit-1)
                val timestampBack=Constant.getLongTimeDate(binding.tvStartDate.text.toString())
                ECareDataPicker(
                    requireActivity(),
                    false,
                    object : ECareDataPicker.PickerCallback {
                        override fun onSelect(date: String?, isCurrentDate: Boolean) {
                            binding.tvEndDate.text = Constant.dateToShow(date.toString())

//                            val diff = Constant.getLongTimeDate(binding.tvEndDate.text.toString()) -
//                                    Constant.getLongTimeDate(binding.tvStartDate.text.toString())
//                            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                            days = Constant.getDateDiff(binding.tvStartDate.text.toString(),binding.tvEndDate.text.toString())
                            days =  calculateDaysAfterHolidays(days)
                            binding.tvNumberDays.text = buildString {
                                append(days  )
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

                        if (it.data != null) {

                            if ( it.data.holidayList !=null){
                            if ( it.data.holidayList.holiday !=null){
                                holidayList = it.data.holidayList.holiday as MutableList<Holiday>

                            }
                            }
                             leaveTerm=it.data.leaveTerms
                            termCondition=it.data.termCondition

                             if (it.data.leaveTypes!=null){
                                 leaveTypeList = it.data.leaveTypes
                                 it.data.leaveTypes.forEach { data ->
                                     leaveTypesDataString.add(data.suggestion)
                                 }
                                 val arrayAdapter = ArrayAdapter(
                                     requireContext(),
                                     android.R.layout.simple_list_item_1,
                                     leaveTypesDataString
                                 )
                                 binding.autoCompleteReason.setAdapter(arrayAdapter)
                             }
                        }

                    }
                }
            }
        }

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
                        if (it.data?.errorCode ==0){
                            findNavController().popBackStack()
                             mainActivity().showMessage("Submitted Successfully!!!")
                        }else{
                             mainActivity().showMessage(it.data!!.message.toString())
                        }
                    }
                }
            }
        }

            leaveApplyLeaveViewModel.leaveSetting()

            binding.autoCompleteReason.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    selectedLeaveTypeID = leaveTypeList[position].lvSgID

                    isAttachamentMandetoy=leaveTypeList[position].attachmentMandatory
                }

            binding.btnSubmit.setOnClickListener {

                if (validateData()) {
                    leaveApplyLeaveViewModel.leaveApply(
                        0,
                         Constant.toSystemDate(binding.tvStartDate.text.toString()),
                        Constant.toSystemDate(binding.tvEndDate.text.toString() ),
                        binding.tvNumberDays.text.toString().toDouble(),
                        null,
                        binding.autoCompleteReason.text.toString(),
                        if (imageString.isNotEmpty()) FileAttachment(imageString, imageExt, "") else null

                    )
                }

            }

            binding.toggleButtonTypeLeave.addOnButtonCheckedListener { _, _, _ ->
                when (binding.toggleButtonTypeLeave.checkedButtonId) {
                    R.id.btn_leave_req -> {
                        binding.llLeaveHistory.isVisible = false
                        binding.llMainLeaveRequest.isVisible = true
                    }

                    else -> {
                        binding.llLeaveHistory.isVisible = true
                        binding.llMainLeaveRequest.isVisible = false
                    }
                }
            }


            /* lifecycleScope.launch {
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

                             if (it.data.dtl != null) {

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


                             }else{
                                 binding.rvLeaveHistory.isVisible = false
                                 binding.tvNoData.isVisible = true
                             }

                             }else{
                                 binding.rvLeaveHistory.isVisible = false
                                 binding.tvNoData.isVisible = true
                             }


                         }
                     }
                 }

             }

             leaveApplyLeaveViewModel.leaveHistory()
     */


        binding.tvTc.setOnClickListener {
            i_agree_dialog()
        }
        }


       private fun calculateDaysAfterHolidays(days: Double):Double{
           var holiday = 0
           var leaveDayCountTemp: Double = days

               for (modelHoliday in holidayList) {
                   if (isDateInBetweenIncludingEndPoints(
                           binding.tvStartDate.text.toString(),binding.tvEndDate.text.toString(),
                           modelHoliday.fromDate
                       )
                   ) {
                       if (modelHoliday.tillDate == "0001-01-01T00:00:00") {
                           holiday += 1
                       } else {
                           if (holidayLastDateGreaterSelectLastDate(
                                   modelHoliday.tillDate,
                                   binding.tvEndDate.text.toString()
                               )
                           ) {
                               val noOfHolidaysInBetween: Double =
                                   Constant.getDateDiff(modelHoliday.fromDate, binding.tvEndDate.text.toString())
                               val `val` = noOfHolidaysInBetween.toInt()
                               holiday += `val`
                           } else {
                               val noOfHolidays: Double = Constant.getDateDiff(
                                   modelHoliday.fromDate,
                                   modelHoliday.tillDate
                               )
                               val `val` = noOfHolidays.toInt()
                               holiday += `val`
                           }
                       }
                   }
               }
             return    leaveDayCountTemp - holiday

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

                        imageExt =
                            FileAccess.getImageExtFromUri(requireContext(), bitmap).toString()
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
            }else
            if (binding.tvEndDate.text.toString().isEmpty()) {
                validate = false
                mainActivity().showMessage("Select To Date")

            }else
            if (selectedLeaveTypeID == 0) {
                validate = false
                mainActivity().showMessage("Select Reason")

            }else
             if (!binding.cbLeaveTc.isChecked) {
                validate = false
                mainActivity().showMessage("Please Check Term and Condition")

            }else
            if (isAttachamentMandetoy  ) {
                if (imageString.isEmpty()) {
                    validate = false
                    mainActivity().showMessage("Please Upload Attachment")
                }

            }

            return validate
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



    }