package com.app.ecarepro.ui.sms_app_msg_report.recharge_log

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentRechareLogBinding
import com.app.ecarepro.ui.MainActivity
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class RechargeLogFragment : Fragment() {

    private lateinit var binding: FragmentRechareLogBinding
    private val rechargeLogViewModel : RechargeLogViewModel by viewModels()

    private val dateFrom: Calendar = Calendar.getInstance()

    private val dateTo: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         return inflater.inflate(R.layout.fragment_rechare_log, container, false)
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
            updateDateFilterText()
        }
    }

    private fun updateDateFilterText() {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dateFormat.format(Date(dateFrom.timeInMillis))
        val from = dateFormat.format(Date(dateFrom.timeInMillis))
        val to = dateFormat.format(Date(dateTo.timeInMillis))

        binding.apply {
            dateFrom.text = from
            dateTo.text = to
        }
        getRechargeLog(
            binding.dateFrom.text.toString(),
            binding.dateTo.text.toString()
        )
    }

    private fun getRechargeLog( fromDate: String, toDate: String ) {
        lifecycleScope.launch {
            rechargeLogViewModel.rechargeLogStateFlow.collectLatest {
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

                            if (it.data.rechargeLog != null) {

                                binding.recyclerSmsUsageReport.isVisible = true
                                binding.tvNoData.isVisible = false

                                val rechargeLogAdapter = RechargeLogAdapter(
                                    it.data.rechargeLog,
                                    this@RechargeLogFragment
                                )

                                binding.recyclerSmsUsageReport.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = rechargeLogAdapter
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
        rechargeLogViewModel.getRechargeLog(fromDate, toDate)

    }

}