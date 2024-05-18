package com.app.ecarepro.ui.attendance_section

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.app.ecarepro.model.MyClasseX
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.staffAttendence.AttendanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ShowAttendanceFragment : Fragment() {
    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private lateinit var binding: FragmentShowAttendanceBinding
    private val sessionAdapter by lazy {
        ArrayAdapter<AcademicYear>(requireContext(), android.R.layout.simple_spinner_item).apply {
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentShowAttendanceBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.autoCompleteYear.setAdapter(sessionAdapter)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            attendanceViewModel.attendanceStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
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
                            sessionAdapter.addAll(it.data.academicYears)
                            binding.rvAttendence.isVisible = true
                            binding.tvNoData.isVisible = false

                            val noticeAdapter =
                                ShowAttendanceListAdapter(
                                    it.data.attendance
                                )

                            binding.rvAttendence.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(activity)
                                adapter = noticeAdapter
                            }

                        }

                    }


                }
            }
        }

        attendanceViewModel.getAttendance("2022-02-01","2022-03-31","5")

    }

}