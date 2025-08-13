package com.app.ecarepro.ui.report

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class StudentAttendanceSummeryFragment : Fragment(), ItemListener<ClassSummary> {
    private var selectedDate: String? = null

    private lateinit var binding: FragmentStudentAttedanceReportBinding
    private val studentAttRepoViewModel: StudentAttSummeryViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentAttedanceReportBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.attendance_summary)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDate.text = Constant.currentDate()

        binding.tvDate.setOnClickListener {
// When opening the picker:
            val picker =
                ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                    override fun onSelect(date: String?, isCurrentDate: Boolean) {
                        selectedDate = date // store selected date
                        binding.tvDate.text = Constant.dateToShow(date.toString())
                        studentAttRepoViewModel.getAttendanceSummary(
                            Constant.toSystemDate(binding.tvDate.text.toString())
                        )
                    }
                })

// Set max date
            picker.setMaxDate(Constant.getLongTimeDate(Constant.currentDate()))

// Now use reflection to get access to the private dpd field
            try {
                val field = picker.javaClass.getDeclaredField("dpd")
                field.isAccessible = true
                val datePickerDialog = field.get(picker) as DatePickerDialog

                // If we have previously selected date, set it manually
                selectedDate?.let {
                    val parts = it.split("-")
                    val year = parts[0].toInt()
                    val month = parts[1].toInt() - 1
                    val day = parts[2].toInt()
                    datePickerDialog.updateDate(year, month, day)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            /*  ECareDataPicker(requireActivity(), false, object : ECareDataPicker.PickerCallback {
                  override fun onSelect(date: String?, isCurrentDate: Boolean) {
                      selectedDate = date // 🔐 Save for later
                      binding.tvDate.text = Constant.dateToShow(date.toString())
                      studentAttRepoViewModel.getAttendanceSummary(Constant.toSystemDate(binding.tvDate.text.toString()))
                  }
              }).setMaxDate(Constant.getLongTimeDate(Constant.currentDate()))*/
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
                                        this@StudentAttendanceSummeryFragment,
                                        it.data.isLateEnabled
                                    )

                                    binding.rvAttReport.apply {
                                        setHasFixedSize(true)
                                        layoutManager = GridLayoutManager(activity, 2)
                                        adapter = studentRepoAttAdapter
                                    }
                                } else {
                                    binding.nestedScrollView.isVisible = false
                                    binding.tvNoData.isVisible = true

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
        val totalStudent: Int =
            data.totalPresent + data.totalAbsent + data.totalLeave + data.totalNA + data.totalWH


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
                tvPresentWorkingCount.text = buildString {
                    append(data.totalPresent_WH)
                }
                tvNACount.text = buildString {
                    append(data.totalNA)
                }
                tvWHCount.text = buildString {
                    append(data.totalWH)
                }

                try {

                    tvWHPer.text = buildString {

                        append(setCalculatedPercentageToInt(data.totalWH, totalStudent))

                        append("%")
                    }

                    tvPresentPer.text = buildString {

                        append(setCalculatedPercentageToInt(data.totalPresent, totalStudent))

                        append("%")
                    }
                    tvPresentWorkingHoliday.text = buildString {

                        append(setCalculatedPercentageToInt(data.totalPresent_WH, totalStudent))

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
                    tvNaPer.text = buildString {

                        append(setCalculatedPercentageToInt(data.totalNA, totalStudent))

                        append("%")
                    }

//                    binding.pieChartView.aa_drawChartWithChartModel(
//                        getBarChartModel(
//                            setCalculatedPercentageToInt(data.totalPresent, totalStudent),
//                            setCalculatedPercentageToInt(data.totalLeave, totalStudent),
//                            setCalculatedPercentageToInt(data.totalAbsent, totalStudent),
//                            setCalculatedPercentageToInt(data.totalLate, totalStudent),
//
//                            )
//                    )
                    showPieChart(
                        data.isLateEnabled,
                        data.totalPresent,
                        data.totalAbsent,
                        data.totalLeave,
                        data.totalLate,
                        data.totalNA,
                        data.totalWH,

                        )

                } catch (_: Exception) {

                }


            }
        } else {
            binding.llMain.isVisible = false
        }


    }

    private fun getBarChartModel(present: Double, leave: Double, absent: Double, late: Double) =
        AAChartModel()

        .chartType(AAChartType.Pie)
        .dataLabelsEnabled(true)
        .colorsTheme(
            arrayOf("#4DAC3C","#FF352F","#FFD700","#FFFEA11C")
        )
        .series(
            arrayOf(
                AASeriesElement()
                    .name(getString(R.string.student))
                    .size("80%")
                    .innerSize("70%")
                    .borderWidth(0)
                    .allowPointSelect(false)
                    .data(
                        arrayOf(
                            arrayOf(getString(R.string.present), present),
                            arrayOf(getString(R.string.general_absent), absent),
                            arrayOf(getString(R.string.leave), leave),
                            arrayOf(getString(R.string.late), late)
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


    private fun showPieChart(
        isLate: Boolean,
        totalPresent: Int,
        totalAbsent: Int,
        totalLeave: Int,
        totalLate: Int,
        totalNA: Int,
        totalWH: Int
    ) {
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.setUsePercentValues(false)
        binding.pieChart.isRotationEnabled = false
        binding.pieChart.setDrawMarkerViews(false)
        val yvalues = ArrayList<PieEntry>()
        if (!isLate) {
            yvalues.add(PieEntry(totalPresent.toFloat(), 0))
            yvalues.add(PieEntry(totalAbsent.toFloat(), 1))
            yvalues.add(PieEntry(totalLeave.toFloat(), 2))
            yvalues.add(PieEntry(totalNA.toFloat(), 3))
            yvalues.add(PieEntry(totalWH.toFloat(), 4))
        } else {
            yvalues.add(PieEntry((totalPresent - totalLate).toFloat(), 0))
            yvalues.add(PieEntry(totalAbsent.toFloat(), 1))
            yvalues.add(PieEntry(totalLeave.toFloat(), 2))
            yvalues.add(PieEntry(totalLate.toFloat(), 3))
            yvalues.add(PieEntry(totalNA.toFloat(), 4))
            yvalues.add(PieEntry(totalWH.toFloat(), 5))
        }

        val dataSet = PieDataSet(yvalues, "")
        dataSet.sliceSpace = 2f
        val xVals = ArrayList<String>()
        xVals.add("")
        xVals.add("")
        val data = PieData(dataSet)
        // data.setValueFormatter(new PercentFormatter());
        binding.pieChart.setData(data)
        if (!isLate) dataSet.setColors(
            resources.getColor(R.color.disabled),
            resources.getColor(R.color.absent_red),
            resources.getColor(R.color.att_leave_color),
            resources.getColor(R.color.att_na_color),
            resources.getColor(R.color.category7)
        )
        else dataSet.setColors(
            resources.getColor(R.color.disabled),
            resources.getColor(R.color.absent_red),
            resources.getColor(R.color.att_leave_color),
            resources.getColor(R.color.att_late_color),
            resources.getColor(R.color.att_na_color),
            resources.getColor(R.color.category7),

            )

        data.setValueTextSize(13f)
        data.setDrawValues(false)
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.animateXY(1400, 1400)

        val s = "${totalPresent + totalAbsent + totalLeave  + totalNA+ totalWH}"+ getString(R.string.student_s).trimIndent()

        val length = (totalPresent + totalAbsent + totalLeave + totalNA + totalWH).toString() + ""
        val ss1 = SpannableString(s)
        ss1.setSpan(RelativeSizeSpan(2f), 0, length.length, 0) // set size
        ss1.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.deep_black)),
            0,
            3,
            0
        ) // set color

        binding.pieChart.centerText = ss1
        binding.pieChart.setCenterTextSize(16f)
        binding.pieChart.setCenterTextColor(resources.getColor(R.color.deep_black))
        binding.pieChart.holeRadius = 70f
        binding.pieChart.description = null
    }


}