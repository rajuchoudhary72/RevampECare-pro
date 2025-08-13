package com.app.ecarepro.ui.collection_report

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import androidx.navigation.fragment.findNavController

import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.CollectionReportBinding
import com.app.ecarepro.estimateCollectionCard
import com.app.ecarepro.model.CollectionReport

import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.fee_report.collection.CollectionFeeReportListAdapter
import com.app.ecarepro.ui.fee_report.collection.CollectionFeeReportViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class CollectionReportFragment : Fragment() {
    private lateinit var noticeAdapter: CollectionReportAdapter
    private lateinit var binding: CollectionReportBinding
    private val dateFrom: Calendar = Calendar.getInstance()
    private val collectionFeeReportViewModel: CollectionFeeReportViewModel by viewModels()
    private val dateTo: Calendar = Calendar.getInstance()
    private val list = mutableListOf<CollectionReport>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CollectionReportBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner

        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        noticeAdapter = CollectionReportAdapter(
            list,
            this@CollectionReportFragment
        )

        binding.recyclerCollectionReport.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = noticeAdapter
        }
        binding.ivDatePicker.setOnClickListener {
            showDateRangePicker()
        }
        return binding.root
    }

    private fun showDateRangePicker() {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setCalendarConstraints(constraintsBuilder.build())
                .setTitleText("Select dates")
                .setSelection(
                    androidx.core.util.Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()
        dateRangePicker.addOnPositiveButtonClickListener {
            dateFrom.timeInMillis = it.first
            dateTo.timeInMillis = it.second
            updateDateFilterText(true)
        }

        dateRangePicker.show(parentFragmentManager, "date_range_picker")
    }

    private fun updateDateFilterText(setAsFilter: Boolean = false) {
        binding.llDateRange.isVisible=true
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        dateFormat.format(Date(dateFrom.timeInMillis))
        val from = dateFormat.format(Date(dateFrom.timeInMillis))
        val to = dateFormat.format(Date(dateTo.timeInMillis))

        binding.apply {
            tvFrom.text = from
            tvTo.text = to
        }
        getFeeCollection()


    }


    private fun getFeeCollection() {
        list.clear()
        lifecycleScope.launch {
            collectionFeeReportViewModel.feeCollectionStateFlow.collectLatest { it ->
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.recyclerCollectionReport.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerCollectionReport.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerCollectionReport.isVisible = true

                        if (it.data != null) {
                            list.clear()
                            binding.recyclerCollectionReport.isVisible = true
                            binding.tvNoData.isVisible = false
                            list.addAll(it.data.collectionReportList)
                            val totalCost  = list.sumOf {item-> item.amount.toDouble()}
                            binding.tvTotal.text = getString(R.string.rs, totalCost.toString())
                            binding.bottomAmount.isVisible=true
                            noticeAdapter.notifyDataSetChanged()
                        }

                    }

                    else -> {}
                }
            }
        }
        collectionFeeReportViewModel.feeCollectionReport(
            normalizeDate(binding.tvFrom.text.toString().changeDateFormat()),
            normalizeDate( binding.tvTo.text.toString().changeDateFormat())
        )
    }

    private fun String.changeDateFormat(): String {
        val inputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy",Locale.getDefault())
        val inputDateStr = this
        val date = inputFormat.parse(inputDateStr)
        return date?.let { outputFormat.format(it) } ?: this
    }

    fun normalizeDate(dateStr: String): String {
        val monthMap = mapOf(
            "जनवरी" to "Jan",
            "फ़रवरी" to "Feb",
            "फरवरी" to "Feb", // without nukta
            "मार्च" to "Mar",
            "अप्रैल" to "Apr",
            "मई" to "May",
            "जून" to "Jun",
            "जुलाई" to "Jul",
            "अगस्त" to "Aug",
            "अग" to "Aug", // short form
            "सितम्बर" to "Sep",
            "सितंबर" to "Sep",
            "अक्तूबर" to "Oct",
            "अक्टूबर" to "Oct",
            "नवम्बर" to "Nov",
            "नवंबर" to "Nov",
            "दिसम्बर" to "Dec",
            "दिसंबर" to "Dec"
        )

        val parts = dateStr.trim().split("\\s+".toRegex())
        if (parts.size < 3) return dateStr // not a valid date format

        val monthHindi = parts[1]
        return if (monthMap.containsKey(monthHindi)) {
            parts.toMutableList().apply { this[1] = monthMap[monthHindi]!! }.joinToString(" ")
        } else {
            dateStr // already English or unsupported month
        }
    }

    // Example usage:






}

