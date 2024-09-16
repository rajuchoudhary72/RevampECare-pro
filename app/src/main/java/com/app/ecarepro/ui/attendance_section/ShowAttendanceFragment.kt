package com.app.ecarepro.ui.attendance_section

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentShowAttendanceBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.staffAttendence.AttendanceViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.lassi.common.extenstions.hide
import com.lassi.common.extenstions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class ShowAttendanceFragment : Fragment() {
    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private lateinit var binding: FragmentShowAttendanceBinding
    private val list = mutableListOf<Attendance>()
    private val noticeAdapter by lazy { ShowAttendanceListAdapter(list) }
    private val sessionAdapter by lazy {
        ArrayAdapter<AcademicYear>(requireContext(), android.R.layout.simple_spinner_item).apply {
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
    private val dateFrom: Calendar = Calendar.getInstance()
    val dateFormateForApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTo: Calendar = Calendar.getInstance()
    private var yId = 0
    private var toFragment: String = ""
    private var toStartDate: String = ""
    private var toEndDate: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentShowAttendanceBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.autoCompleteYear.setAdapter(sessionAdapter)
        try {
            toFragment = requireArguments().getString("studentID").toString()
            toStartDate = requireArguments().getString("formDate").toString()
            toEndDate = requireArguments().getString("tillDate").toString()
        }catch (e:IllegalStateException){
            e.message
        }
        binding.dateRange.setOnClickListener {
            pickDateRange()
        }
        dateFrom.set(Calendar.DAY_OF_MONTH, 1)
        binding.rvAttendence.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = noticeAdapter
        }
        updateDateFilterText()
        callApi()
        binding.tvSession.text = getString(R.string.attendance_in_session_2021_2022, "")

        binding.autoCompleteYear.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, pos, id ->

                yId = sessionAdapter.getItem(pos)?.yrID ?: 0
                val session = sessionAdapter.getItem(pos)?.session ?: ""
                binding.tvSession.text =
                    getString(R.string.attendance_in_session_2021_2022, session)
                callApi()

            }
        return binding.root
    }

    private fun updateDateFilterText(setAsFilter: Boolean = false) {
        val dateFormate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dateFormate.format(Date(dateFrom.timeInMillis))
        val from = dateFormate.format(Date(dateFrom.timeInMillis))
        val to = dateFormate.format(Date(dateTo.timeInMillis))



        binding.apply {

            if (toFragment != null) {
                dateRange.setText("$toStartDate - $toEndDate")
                tvHeadingDateRange.text = getString(
                    R.string.attendance_between_01_aug_2021_to_09_oct_2021,
                    "$toStartDate to $toEndDate"
                )
            } else {
                dateRange.setText("$from - $to")
                tvHeadingDateRange.text = getString(
                    R.string.attendance_between_01_aug_2021_to_09_oct_2021,
                    "$from to $to"
                )
            }

        }
        if (setAsFilter)
            callApi()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            attendanceViewModel.attendanceStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        list.clear()
                        noticeAdapter.notifyItemRangeRemoved(0, list.size)
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvAttendence.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAttendence.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAttendence.isVisible = true

                        if (it.data != null) {

                            binding.data = it.data
                            binding.invalidateAll()
                            sessionAdapter.clear()
                            sessionAdapter.addAll(it.data.academicYears)
                            binding.rvAttendence.isVisible = true
                            binding.tvNoData.isVisible = false
                            it.data.attendance?.let { it1 ->
                                binding.tvNoData.hide()
                                binding.rvAttendence.show()
                                list.addAll(it1)
                            }
                                ?: run {

                                    binding.tvNoData.show()
                                    binding.rvAttendence.hide()
                                }
                            noticeAdapter.notifyItemChanged(0, list.size)


                        }

                    }


                }
            }
        }


    }

    private fun callApi() {
        if (toFragment != null) {
            attendanceViewModel.getAttendance(toStartDate, toEndDate, "$yId", toFragment)
        } else {
            attendanceViewModel.getAttendance(
                dateFormateForApi.format(Date(dateFrom.timeInMillis)),
                dateFormateForApi.format(Date(dateTo.timeInMillis)),
                "$yId",
                toFragment
            )
        }
    }

}