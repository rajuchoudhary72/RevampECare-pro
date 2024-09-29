package com.app.ecarepro.ui.report.student_att_report

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentAttRepo
import com.app.ecarepro.databinding.FragmentStudentAttRepoBinding
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.report.StudentRepoAttAdapter
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class StudentAttRepoFragment : Fragment() {

    var id: String = ""

    private lateinit var binding : FragmentStudentAttRepoBinding
    private val studentAttRepoViewModel: StudentAttRepoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentStudentAttRepoBinding.inflate(inflater,container,false)
         id = requireArguments().getString(Constant.ID).toString()
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            binding.tvStartDate.text=Constant.currentDate()
            binding.tvEndDate.text=Constant.currentDate()

            getStudentAttendance()

            binding.llStart.setOnClickListener {
                ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                    override fun onSelect(date: String?, isCurrentDate: Boolean) {
                        binding.tvStartDate.text = Constant.dateToShow(date.toString())
                        getStudentAttendance()
                     }

                }).setMaxDate(Constant.getLongTimeDate(Constant.currentDate()))
            }
            binding.llEnd.setOnClickListener {
                ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                    override fun onSelect(date: String?, isCurrentDate: Boolean) {
                        binding.tvEndDate.text = Constant.dateToShow(date.toString())
                        getStudentAttendance()
                    }

                }).setMaxDate(Constant.getLongTimeDate(Constant.currentDate()))
            }
        }




    }

    fun getStudentAttendance(){

        lifecycleScope.launch {
            studentAttRepoViewModel.studentAttStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.rvAttendanceList.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAttendanceList.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.rvAttendanceList.isVisible = true

                        if (it.data != null) {

                            setupAttDetails(it.data)

                            if (it.data.attendance!=null) {
                                binding.rvAttendanceList.isVisible = true

                                val studentRepoAttAdapter = StudentAttAdapter(
                                    it.data.attendance,
                                    this@StudentAttRepoFragment
                                )

                                binding.rvAttendanceList.apply {
                                    setHasFixedSize(true)
                                    layoutManager = GridLayoutManager(activity, 2)
                                    adapter = studentRepoAttAdapter
                                }
                            } else {
                                binding.rvAttendanceList.isVisible = false
                            }

                        }

                    }


                }


            }

        }

        studentAttRepoViewModel.getStudentAttendance( Constant.toSystemDate(binding.tvStartDate.text.toString()),
            Constant.toSystemDate( binding.tvEndDate.text.toString()),"0",id)


    }


    private fun setupAttDetails(data: NetworkStudentAttRepo) {
        with(binding) {
            workingDay.text=data.workingDays.toString()
            presentDay.text=data.presentDays.toString()
            absentDay.text=data.absentDays.toString()
            leaveDay.text=data.leaveDays.toString()
            tvLateCount.text=data.lateDays.toString()

             totalSchoolDay.text=data.schoolDays.toString()
            totalPresentDay.text=data.totalPresent.toString()
            totalAbsentDay.text=data.totalAbsent.toString()
            totalLeaveDay.text=data.totalLeave.toString()
            tvLateMonthCount.text=data.totalLates.toString()



            try {
                perPresent.text = buildString {

                    append(setCalculatedPercentageToInt(data.totalPresent, data.schoolDays))
                    append("%")  }
                perAbsent.text = buildString {

                    append(setCalculatedPercentageToInt(data.totalAbsent, data.schoolDays))

                    append("%")  }
                perLeave.text = buildString {

                    append(setCalculatedPercentageToInt(data.totalLeave, data.schoolDays))

                    append("%")  }
                tvLateCircle.text = buildString {

                    append(setCalculatedPercentageToInt(data.totalLates, data.schoolDays))

                    append("%")  }
            }catch (_:Exception){ }

        }
    }

    private fun setCalculatedPercentageToInt(day: Int, totalDay: Int): Double {
        return ((day * 100.00 / totalDay * 100.00).roundToInt() / 100.00)
    }

}