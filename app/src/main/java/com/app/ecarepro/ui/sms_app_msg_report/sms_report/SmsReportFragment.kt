 package com.app.ecarepro.ui.sms_app_msg_report.sms_report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.SmS
import com.app.ecarepro.databinding.FragmentSmsReportBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.leave.leave_report.LeaveReportAdapter
import com.app.ecarepro.utils.Constant
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


 @AndroidEntryPoint
 class SmsReportFragment : Fragment() {


     private var smsTypeId: Int = 0
     private lateinit var binding: FragmentSmsReportBinding
    private val viewModel: SmsReportViewModel by viewModels()

     private val dateFrom: Calendar = Calendar.getInstance()
    private val dateTo: Calendar = Calendar.getInstance()

     private var pageIndex: Int = 1
     private var pastVisiblesItems: Int = 0
     private var totalItemCount: Int = 0
     private var visibleItemCount: Int = 0
     private var isLoading: Boolean = true

     private lateinit var smsReportAdapter: SmsReportAdapter
     private var smsReportList = mutableListOf<SmS>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
       binding = FragmentSmsReportBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

         smsReportAdapter = SmsReportAdapter( smsReportList ,this)
         with(binding) {

             binding.recyclerViewSmsReport.apply {
                 setHasFixedSize(true)
                 layoutManager = LinearLayoutManager(activity)
                 adapter = smsReportAdapter
             }
         }

         binding.apply {

             binding.tvDateRange.setText(buildString {
                 append(Constant.currentDate())
                 append(" - ")
                 append(Constant.currentDate())
             })
             tvDateRange.setOnClickListener { pickDateRange() }

         }

         lifecycleScope.launch {
             viewModel.smsReportStateFlow.collect { networkResult ->
                 when (networkResult) {
                     is NetworkResult.Loading -> {
                         (requireActivity() as MainActivity).showLoader(true)
                     }

                     is NetworkResult.Error -> {
                         (requireActivity() as MainActivity).showLoader(false)
                     }

                     is NetworkResult.Success -> {
                         (requireActivity() as MainActivity).showLoader(false)
                         if (networkResult.data != null) {
                             if (networkResult.data.smSs != null) {
                                 (requireActivity() as MainActivity).showLoader(false)

                                 if ( networkResult.data.smSs.isNotEmpty()) {

                                     binding.recyclerViewSmsReport.isVisible = true
                                     binding.tvNoData.isVisible = false

                                     if (pageIndex == 1) {
                                         smsReportAdapter.clearData()
                                     }
                                     isLoading=true
                                     smsReportAdapter.setData(
                                         networkResult.data.smSs as MutableList<SmS>
                                     )

                                 } else {
                                     if (pageIndex == 1) {
                                         binding.recyclerViewSmsReport.isVisible = false
                                         binding.tvNoData.isVisible = true
                                     }

                                 }
                             } else {
                                 if (pageIndex == 1) {
                                     binding.recyclerViewSmsReport.isVisible = false
                                     binding.tvNoData.isVisible = true
                                 }

                             }
                         }
                     }


                     else -> {}
                 }
             }
         }

         setupRecycleViewPager()
         getSMSType( )

         getSMSReport( )

     }
     private fun getSMSReport() {
         val inputString = binding.tvDateRange.text.toString()
         val pattern = "(\\d{2} \\w{3} \\d{4}) - (\\d{2} \\w{3} \\d{4})".toRegex()
         val matchResult = pattern.find(inputString)

         val firstDateString = matchResult?.groupValues?.get(1) // Get the first date string
         val secondDateString = matchResult?.groupValues?.get(2)
         viewModel.getSMSReport(Constant.toSystemDate(firstDateString.toString()),
             Constant.toSystemDate(secondDateString.toString()),smsTypeId,pageIndex)
     }


     private fun getSMSType() {
         lifecycleScope.launch {
             viewModel.smsTypeStateFlow.collect { networkResult ->
                 when (networkResult) {
                     is NetworkResult.Loading -> {
                         (requireActivity() as MainActivity).showLoader(true)
                     } is NetworkResult.Error -> {
                         (requireActivity() as MainActivity).showLoader(false)
                     } is NetworkResult.Success -> {
                         (requireActivity() as MainActivity).showLoader(false)
                         if (networkResult.data != null) {
                             if (networkResult.data.smsType!=null)  {
                                 val adapter = ArrayAdapter(
                                     requireContext(),
                                     android.R.layout.simple_list_item_1,
                                     networkResult.data.smsType.map { it.subject })
                                 binding.autoCompleteSmsType.setAdapter(adapter)
                                 binding.autoCompleteSmsType.setOnItemClickListener { _, _, position, _ ->

                                     smsTypeId = networkResult.data.smsType[position].typeID!!
                                     pageIndex=1
                                     getSMSReport()
                                 } } } }

                     else -> {}
                 }  } }
         viewModel.getSMSType()
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
         val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
         dateFormat.format(Date(dateFrom.timeInMillis))
         val from = dateFormat.format(Date(dateFrom.timeInMillis))
         val to = dateFormat.format(Date(dateTo.timeInMillis))


         binding.tvDateRange.setText(buildString {
             append(from)
             append(" - ")
             append(to)
         })
         pageIndex=1
         getSMSReport()
     }

     private fun setupRecycleViewPager() {
         smsReportAdapter.clearData()
         binding.recyclerViewSmsReport.addOnScrollListener(object :
             RecyclerView.OnScrollListener() {

             override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                 super.onScrolled(recyclerView, dx, dy)
                 val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                 if (linearLayoutManager != null) {
                     if (dy > 0) {
                         visibleItemCount = linearLayoutManager.childCount;
                         totalItemCount = linearLayoutManager.itemCount;
                         pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()

                         if (isLoading) {
                             if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                 isLoading = false
                                 pageIndex += 1
                                 getSMSReport()
                             }
                         }

                     }
                 }
             }
         })



     }

 }