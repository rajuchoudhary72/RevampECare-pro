package com.app.ecarepro.ui.staff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.StaffTypeDto
import com.app.ecarepro.databinding.FragmentStaffAttendanceBinding
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.staffAttendance
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.selectRecipients.SelectStaffTypesFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


@AndroidEntryPoint
class StaffAttendanceFragment : Fragment() {

    private var _binding: FragmentStaffAttendanceBinding? = null

    private val binding get() = _binding!!

    private val viewModel: StaffAttendanceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStaffAttendanceBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@StaffAttendanceFragment.viewModel
        }
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setObservers()
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {

            launch {
                viewModel.loadState.collectLatest {
                    mainActivity().showLoader(it is LoadingState.Loading)
                    when (it) {
                        is LoadingState.Error -> {
                            mainActivity().showMessage(it.error.message ?: "")
                            findNavController().popBackStack()
                        }

                        else -> {}
                    }
                }
            }

            launch {
                viewModel.date.collectLatest {
                    binding.btnSelectDate.text = it
                }
            }

            launch {
                viewModel.uiState.collectLatest {
                    buildModels(it)
                }
            }

        }
    }

    private fun buildModels(uiState: StaffAttendanceUiState.Success) {
        binding.recyclerView.withModels {
            if (uiState.attendance.isEmpty()) {
                noDataFoundView {
                    id("r")
                }
            } else {
                uiState.attendance.forEach {
                    staffAttendance {
                        id(it.empID)
                        attendance(it)
                    }
                }
            }
        }
    }

    private fun setUpViews() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSelectDate.setOnClickListener {
            selectDate()
        }

        binding.btnSortByName.setOnClickListener {
            viewModel.toggleSortByName()
        }

        binding.btnSortByDesignation.setOnClickListener {
            viewModel.toggleSortByDesignation()
        }

        binding.btnFilter.setOnClickListener {
            selectStaffType()
        }

        binding.recyclerView.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelOffset(R.dimen.vertical_margin)
            )
        )

        binding.toggleButtonAttendanceType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_all -> {
                        viewModel.setAttendanceType(AttendanceType.ALL)
                    }
                    R.id.btn_present -> {
                        viewModel.setAttendanceType(AttendanceType.PRESENT)
                    }
                    else -> {
                        viewModel.setAttendanceType(AttendanceType.ABSENT)
                    }
                }
            }
        };
    }

    private fun selectStaffType() {
        SelectStaffTypesFragment
            .getInstance(
                StaffTypeDto(
                    staffType = viewModel.staffTypes.value,
                    selectedStaffType = viewModel.selectStaffType.value?.let { listOf(it) }
                ),
                multiSelectionEnabled = false
            )
            .onContactSelected {
                it.firstOrNull()?.let { staffType -> viewModel.selectStaffType(staffType) }
            }
            .show(childFragmentManager, "")
    }

    private fun selectDate() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val minYear = currentYear - 10

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setStart(getCalendarConstraints(minYear, 1, 1).timeInMillis)
                    .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedTime: Long ->
            viewModel.selectDate(Date(selectedTime))
        }

        datePicker.show(childFragmentManager, "datePicker")
    }

    private fun getCalendarConstraints(year: Int, month: Int, day: Int): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        return calendar
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}