package com.app.ecarepro.ui.sms_app_msg_report

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.MyClasseItem
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.WingLST
import com.app.ecarepro.databinding.FragmentSmsMsgReportBinding
import com.app.ecarepro.model.UsesRPT
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.assignment.staff.postAssignment.ClassListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class SmsMsgReportFragment : Fragment() {

    private var allSelected: Boolean = false
    private lateinit var binding: FragmentSmsMsgReportBinding
    private val smsMsgReportViewModel: SmsMsgReportViewModel by viewModels()
    private val dateFrom: Calendar = Calendar.getInstance()
    private var isDateSelected=false
    private var toFragment: String= ""
    private var isWingSelected: Boolean = false
    private lateinit var wingLSTS: List<WingLST>

    var ids = StringBuilder()
    var selectAll: Boolean = false



    private val dateTo: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSmsMsgReportBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        try {
            toFragment= requireArguments().getString(Constant.TO).toString()
            if (toFragment==Constant.FRA_APP_SMS){
                binding.toolbar.title="SMS Uses"
                binding.tvType.text= requireContext().getString(R.string.sms_count)
            }else if (toFragment==Constant.FRA_APP_MESSAGE){
                binding.toolbar.title="App Message Uses"
                binding.tvType.text= requireContext().getString(R.string.message_count)
            }
        }catch (_:Exception){}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getWingReport()

        binding.apply {
            tvSelectWing.setOnClickListener {
                if (!wingLSTS.isNullOrEmpty()){
                    popUpSelectWing()
                }else{
                    Toast.makeText(requireContext(), "No Wing Data", Toast.LENGTH_SHORT).show()
                }
            }
            dateFrom.setOnClickListener { pickDateRange() }
            dateTo.setOnClickListener { pickDateRange() }
        }


    }

    private fun pickDateRange() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setSelection(androidx.core.util.Pair(dateFrom.timeInMillis, dateTo.timeInMillis))

        val picker = builder.build()
        picker.show(activity?.supportFragmentManager!!, picker.toString())

        picker.addOnNegativeButtonClickListener { picker.dismiss() }
        picker.addOnPositiveButtonClickListener {
            dateFrom.timeInMillis = it.first
            dateTo.timeInMillis = it.second
            updateDateFilterText(true)
            isDateSelected=true
        }
    }

    private fun updateDateFilterText(setAsFilter: Boolean = false) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dateFormat.format(Date(dateFrom.timeInMillis))
        val from = dateFormat.format(Date(dateFrom.timeInMillis))
        val to = dateFormat.format(Date(dateTo.timeInMillis))

        binding.apply {
            dateFrom.text = from
            dateTo.text = to
        }
        if (allSelected) {
            getAppMsgUses(
                binding.dateFrom.text.toString(),
                binding.dateTo.text.toString(),
                ""
            )
        } else {

                 if (isWingSelected){
                     getAppMsgUses(
                         binding.dateFrom.text.toString(),
                         binding.dateTo.text.toString(),
                         ids.toString()
                     )
            }

        }
    }




    private fun popUpSelectWing(){

        val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text= getText(R.string.lbl_select_class)
        val  llSelectAll = view.findViewById<LinearLayout>(R.id.llSelectAll)
        val  checkImage = view.findViewById<ImageView>(R.id.checkImage)
        llSelectAll.isVisible=true
        builder.setView(view)

        relOk.setOnClickListener {
            if (isWingSelected){


                val name = StringBuilder()
                ids.clear()

                for (item in wingLSTS) {
                    if (item.checked == true) {
                        if (ids.toString().isEmpty()) {
                            ids.append(item.wingId)
                            name.append(item.wingName)
                        } else {
                            ids.append(",").append(item.wingId)
                            name.append(",").append(item.wingName)
                        }
                    }
                }



                binding.tvSelectWing.text= name
                getAppMsgUses(
                    binding.dateFrom.text.toString(),
                    binding.dateTo.text.toString(),
                    ids.toString()
                )
                builder.dismiss()
            }

        }

        val subjectListAdapter= WingListAdapter(wingLSTS, selectAll, true, object : ItemListener<WingLST> {
            override fun onItemClick(t: WingLST, pos: Int, boolean: Boolean) {
                isWingSelected = true
            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = subjectListAdapter
        }

        llSelectAll.setOnClickListener {
            selectAll = !selectAll
            isWingSelected = selectAll
            for (i in wingLSTS) {
                i .checked=selectAll
            }
            subjectListAdapter.notifyDataSetChanged()
            checkImage.setImageResource(if (selectAll) R.drawable.ic_baseline_check_box_24 else R.drawable.ic_baseline_check_box_unselectblank_24)
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun  getWingReport(){
        lifecycleScope.launch {
            smsMsgReportViewModel.wingReportStateFlow.collectLatest {
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
                            wingLSTS = it.data.wingLST

                        }
                    }

                }
            }
        }
        smsMsgReportViewModel.wingsList()
    }

    private fun getAppMsgUses(
        fromDate: String,
        toDate: String,
        iD: String,
    ) {

        if (isDateSelected){
        if (isWingSelected){
            lifecycleScope.launch {
                smsMsgReportViewModel.smsMsgReportStateFlow.collectLatest {
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

                                if (it.data.usesRPT!=null) {
                                if (it.data.usesRPT.isNotEmpty()) {

                                    binding.recyclerSmsUsageReport.isVisible = true
                                    binding.tvNoData.isVisible = false

                                    val outPassReportAdapter = SmsReportAdapter(
                                        it.data.usesRPT,
                                        this@SmsMsgReportFragment
                                    )

                                    binding.recyclerSmsUsageReport.apply {
                                        setHasFixedSize(true)
                                        layoutManager = LinearLayoutManager(activity)
                                        adapter = outPassReportAdapter
                                    }


                                } else {
                                    binding.recyclerSmsUsageReport.isVisible = false
                                    binding.tvNoData.isVisible = true
                                }
                                } else {
                                    binding.recyclerSmsUsageReport.isVisible = false
                                    binding.tvNoData.isVisible = true
                                }

                            }
                        }
                    }
                }
            }
            if (toFragment==Constant.FRA_APP_MESSAGE){
                smsMsgReportViewModel.getAppMsgUses(Constant.toSystemDate(fromDate),Constant.toSystemDate(toDate)  , iD)
            }else{
                smsMsgReportViewModel.getSMSUses(Constant.toSystemDate(fromDate),Constant.toSystemDate(toDate)  , iD)
            }
        }else{
            mainActivity().showMessage("Select Staff")
        }
        }else{
            mainActivity().showMessage("Select Date Range")
        }

    }


}