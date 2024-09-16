package com.app.ecarepro.ui.report

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
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkAttedanceSummary
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentAttedanceReportBinding
import com.app.ecarepro.model.ClassSummary
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.ECareDataPicker
import com.app.ecarepro.utils.listener.ItemListener
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class StudentAttendanceSummeryFragment : Fragment(), ItemListener<ClassSummary> {

    private lateinit var binding: FragmentStudentAttedanceReportBinding
    private val studentAttRepoViewModel: StudentAttSummeryViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentAttedanceReportBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDate.text = Constant.currentDate()

        binding.tvDate.setOnClickListener {
            ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                override fun onSelect(date: String?, isCurrentDate: Boolean) {
                    binding.tvDate.text = Constant.dateToShow(date.toString())
                    studentAttRepoViewModel.getAttendanceSummary(Constant.toSystemDate(binding.tvDate.text.toString()))
                }
            }).setMaxDate(Constant.getLongTimeDate(Constant.currentDate()))
        }

        getStudentAttRepo()

    }



    private fun getStudentAttRepo() {

        lifecycleScope.launch {
            studentAttRepoViewModel.attSummeryStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.nestedScrollView.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.nestedScrollView.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.nestedScrollView.isVisible = true

                        if (it.data != null) {

                            if (it.data.classSummary != null) {
                                setupAttDeatils(it.data)
                                if (it.data.classSummary.isNotEmpty()) {

                                    binding.nestedScrollView.isVisible = true

                                    val studentRepoAttAdapter = StudentRepoAttAdapter(
                                        it.data.classSummary,
                                        this@StudentAttendanceSummeryFragment
                                    )

                                    binding.rvAttReport.apply {
                                        setHasFixedSize(true)
                                        layoutManager = GridLayoutManager(activity, 2)
                                        adapter = studentRepoAttAdapter
                                    }
                                } else {
                                    binding.nestedScrollView.isVisible = false
                                    binding.tvNoData.isVisible=true

                                }
                            }
                        }
                    }
                }
            }
        }

        studentAttRepoViewModel.getAttendanceSummary(Constant.toSystemDate(binding.tvDate.text.toString()))

    }

    private fun setupAttDeatils(data: NetworkAttedanceSummary) {
        val totalStudent: Int = data.totalPresent + data.totalAbsent + data.totalLeave + data.totalLate


        if (totalStudent > 0) {
            binding.llMain.isVisible = true
            with(binding) {

                tvAbsentCount.text = buildString {
                    append(data.totalAbsent)
                }
                tvLateCount.text = buildString {
                    append(data.totalLate)
                }
                tvLeaveCount.text = buildString {
                    append(data.totalLeave)
                }
                tvPresentCount.text = buildString {
                    append(data.totalPresent)
                }

                try {
                    tvPresentPer.text = buildString {

                        append(setCalculatedPercentageToInt(data.totalPresent, totalStudent))

                        append("%")
                    }

                    tvAbsentPer.text = buildString {

                        append(setCalculatedPercentageToInt(data.totalAbsent, totalStudent))
                        append("%")
                    }

                    tvLeavePer.text = buildString {

                        append(setCalculatedPercentageToInt(data.totalLeave, totalStudent))

                        append("%")
                    }
                    tvLatePer.text = buildString {

                        append(setCalculatedPercentageToInt(data.totalLate, totalStudent))

                        append("%")
                    }

                    binding.pieChartView.aa_drawChartWithChartModel(
                        getBarChartModel(
                            setCalculatedPercentageToInt(data.totalPresent, totalStudent),
                            setCalculatedPercentageToInt(data.totalLeave, totalStudent),
                            setCalculatedPercentageToInt(data.totalAbsent, totalStudent),
                            setCalculatedPercentageToInt(data.totalLate, totalStudent),

                            )
                    )


                } catch (_: Exception) {

                }


            }
        }else{
            binding.llMain.isVisible = false
         }


    }

    private fun getBarChartModel(present: Double, leave: Double, absent: Double, late: Double) = AAChartModel()

        .chartType(AAChartType.Pie)
        .dataLabelsEnabled(true)
        .colorsTheme(
            arrayOf("#4DAC3C","#FF352F","#FFD700","#FFFEA11C")
        )
        .series(
            arrayOf(
                AASeriesElement()
                    .name("Student")
                    .size("80%")
                    .innerSize("70%")
                    .borderWidth(0)
                    .allowPointSelect(false)
                    .data(
                        arrayOf(
                            arrayOf("Present", present),
                            arrayOf("Absent", absent),
                            arrayOf("Leave", leave),
                            arrayOf("Late", late)
                        )
                    )
            )
        )

    override fun onItemClick(t: ClassSummary, pos: Int, boolean: Boolean) {
        findNavController().navigate(
            R.id.action_studentAttendanceReportFragment_to_classAttendanceFragment,
            Bundle().apply {
                putString(Constant.CLASS_ID_ARGUMENT, t.id)
                putString(Constant.NAME, t.className)
                putString(Constant.DATE, binding.tvDate.text.toString())
            })
    }

    private fun setCalculatedPercentage(day: Int, totalDay: Int): String {
        return ((day * 100.00 / totalDay * 100.00).roundToInt() / 100.00).toString()
    }

    private fun setCalculatedPercentageToInt(day: Int, totalDay: Int): Double {
        return ((day * 100.00 / totalDay * 100.00).roundToInt() / 100.00)
    }




}