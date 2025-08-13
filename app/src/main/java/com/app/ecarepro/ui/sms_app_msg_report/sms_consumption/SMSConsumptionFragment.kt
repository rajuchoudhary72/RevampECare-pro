package com.app.ecarepro.ui.sms_app_msg_report.sms_consumption

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentSMSConsumptionBinding
import com.app.ecarepro.databinding.FragmentSmsMsgReportBinding
import com.app.ecarepro.model.UsesRPT
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.sms_app_msg_report.SmsMsgReportViewModel
import com.app.ecarepro.ui.sms_app_msg_report.SmsReportAdapter
import com.app.ecarepro.utils.Constant
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class SMSConsumptionFragment : Fragment() {


    private lateinit var binding: FragmentSMSConsumptionBinding
    private val smsConsumptionViewModel: SmsConsumptionViewModel by viewModels()
    private val dateFrom: Calendar = Calendar.getInstance()

    private val dateTo: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSMSConsumptionBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
     }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

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
        }
    }

    private fun updateDateFilterText(setAsFilter: Boolean = false) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        dateFormat.format(Date(dateFrom.timeInMillis))
        val from = dateFormat.format(Date(dateFrom.timeInMillis))
        val to = dateFormat.format(Date(dateTo.timeInMillis))

        binding.apply {
            dateFrom.text = from
            dateTo.text = to
        }
        getAppMsgUses(
            Constant.toSystemDate(binding.dateFrom.text.toString()),
             Constant.toSystemDate(binding.dateTo.text.toString(),)

        )
    }


    private fun getAppMsgUses(
        fromDate: String,
        toDate: String,

    ) {
        lifecycleScope.launch {
            smsConsumptionViewModel.sMSConsumptionStateFlow.collectLatest {
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

                            if (it.data.dateWise != null) {

                                binding.recyclerSmsUsageReport.isVisible = true
                                binding.tvNoData.isVisible = false

                                val outPassReportAdapter = SmsConsumptionAdapter(
                                    it.data.dateWise,
                                    this@SMSConsumptionFragment
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

                        }
                    }
                }
            }
        }
        smsConsumptionViewModel.getSMSConsumption(fromDate, toDate )

    }

}