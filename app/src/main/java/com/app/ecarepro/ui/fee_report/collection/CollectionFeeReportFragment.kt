package com.app.ecarepro.ui.fee_report.collection

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
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentCollectionFeeReportBinding
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
class CollectionFeeReportFragment : Fragment() {

    private lateinit var binding : FragmentCollectionFeeReportBinding
    private val collectionFeeReportViewModel : CollectionFeeReportViewModel by viewModels()
    private val dateFrom: Calendar = Calendar.getInstance()

    private val dateTo: Calendar = Calendar.getInstance()

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentCollectionFeeReportBinding.inflate(inflater,container,false)
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
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dateFormat.format(Date(dateFrom.timeInMillis))
        val from = dateFormat.format(Date(dateFrom.timeInMillis))
        val to = dateFormat.format(Date(dateTo.timeInMillis))

        binding.apply {
            dateFrom.text = from
            dateTo.text = to
        }
        getFeeCollection()


    }



    private fun getFeeCollection(){
        lifecycleScope.launch {
            collectionFeeReportViewModel.feeCollectionStateFlow.collectLatest {
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

                        if (it.data!=null){

                            if (it.data.collectionReportList!=null){

                                binding.recyclerCollectionReport.isVisible=true
                                binding.tvNoData.isVisible=false

                                val noticeAdapter = CollectionFeeReportListAdapter(it.data.collectionReportList ,
                                    this@CollectionFeeReportFragment)

                                binding.recyclerCollectionReport.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = noticeAdapter
                                }
                            }else{
                                binding.recyclerCollectionReport.isVisible=false
                                binding.tvNoData.isVisible=true
                            }

                        }

                    }

                    else -> {}
                }
            }
        }
        collectionFeeReportViewModel.feeCollectionReport("","","","")
    }
}