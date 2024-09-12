package com.app.ecarepro.ui.studentProfile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentProfileAttendanceBinding
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.ProfileAttendanceDTL
import com.app.ecarepro.model.SummaryAttendance
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.calender.ViewPagerAdapter
import com.app.ecarepro.ui.circuler.PopUpListAdapter
import com.app.ecarepro.utils.listener.ItemListener
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class StudentProfileAttendanceFragment(
    private val attendanceDTL: ProfileAttendanceDTL,
    private val academicYears: List<AcademicYear>,
    private val studentID: Int
) : Fragment() ,ItemListener<SummaryAttendance> {

    private lateinit var selectedYearData: AcademicYear
    private lateinit var binding: FragmentStudentProfileAttendanceBinding
    private val studentProfileAttendanceViewModel: StudentProfileAttendanceViewModel by viewModels()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentProfileAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi(attendanceDTL)
        if (academicYears.isNotEmpty()) {
            for (i in academicYears ) {
                if (i.isCur) {
                    binding.ctvSelectYear.text = i.session
                    break
                }
            }
        }


    }


    private fun setCalculatedPercentage(day: Int, totalDay: Int): String {
        return ((day * 100.00 / totalDay * 100.00).roundToInt() / 100.00).toString()
    }

    private fun setCalculatedPercentageToInt(day: Int, totalDay: Int): Double {
        return ((day * 100.00 / totalDay * 100.00).roundToInt() / 100.00)
    }

    private fun getBarChartModel(present: Int, leave: Int, absent: Int, late: Int) = AAChartModel()

        .chartType(AAChartType.Pie)
         .colorsTheme(

             arrayOf("#4DAC3C","#FF352F","#FFD700","#FFFEA11C")
        )
        .dataLabelsEnabled(true)
        .series(
            arrayOf(
                AASeriesElement()

                    .name("Student")
                    .size("60%")
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


    private fun popUpSelectAcademicYears() {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class, null)
        val relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text = "Select Academic Year"
        builder.setView(view)

        relOk.setOnClickListener {
            binding.ctvSelectYear.text = selectedYearData.session
            getAtt()
            builder.dismiss()

        }

        val yearAdapter = PopUpListAdapter(academicYears, object : ItemListener<AcademicYear> {
            override fun onItemClick(t: AcademicYear, pos: Int, boolean: Boolean) {
                selectedYearData = t
            }

        })
        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = yearAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun getAtt() {
        studentProfileAttendanceViewModel.getSAttendanceYrID(  studentID,selectedYearData.yrID )

        lifecycleScope.launch {
            studentProfileAttendanceViewModel.studentProfileStateFlow.collectLatest {
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

                            setupUi(it.data.attDTL)
                        }
                    }
                }
            }


        }
    }


    private fun  setupUi(attendanceDTL: ProfileAttendanceDTL) {

        with(binding) {

            ctvSelectYear.setOnClickListener {
                popUpSelectAcademicYears()
            }

            try {
                tvPresentDay.text = buildString {
                    append("(")
                    append(setCalculatedPercentageToInt(attendanceDTL.present, attendanceDTL.working))
                    append("%)")
                }
                tvAbsentDay.text = buildString {
                    append("(")
                    append(setCalculatedPercentageToInt(attendanceDTL.absent, attendanceDTL.working))
                    append("%)")
                }
                tvLeaveDay.text = buildString {
                    append("(")
                    append(setCalculatedPercentageToInt(attendanceDTL.leave, attendanceDTL.working))
                    append("%)")
                }
                tvLateDay.text = buildString {
                    append("(")
                    append(setCalculatedPercentageToInt(attendanceDTL.late, attendanceDTL.working))
                    append("%)")
                }
                binding.pieChartView. isClearBackgroundColor = true
                binding.pieChartView.aa_drawChartWithChartModel(
                    getBarChartModel(
                        setCalculatedPercentageToInt(attendanceDTL.present, attendanceDTL.working).toInt(),
                        setCalculatedPercentageToInt(attendanceDTL.leave, attendanceDTL.working).toInt(),
                        setCalculatedPercentageToInt(attendanceDTL.absent, attendanceDTL.working).toInt(),
                        setCalculatedPercentageToInt(attendanceDTL.late, attendanceDTL.working).toInt()

                    )
                )
            } catch (_: Exception) {
            }

            tvTotalWorking.text = buildString { append(attendanceDTL.working) }
            tvTotalAbsent.text = buildString { append(attendanceDTL.absent) }
            tvTotalPresent.text = buildString { append(attendanceDTL.present) }
            tvTotalLate.text = buildString { append(attendanceDTL.late) }

            if (attendanceDTL.summaryAttendance != null) {
                val assignmentListAdapter =
                    StudentProfileAtteListAdapter(
                        attendanceDTL.summaryAttendance,
                        this@StudentProfileAttendanceFragment
                    )

                binding.rvAttendanceDetail.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = assignmentListAdapter
                }
                binding.rlMain.isVisible = true
                binding.tvNoData.isVisible = false


            } else {
                binding.rlMain.isVisible = false
                binding.tvNoData.isVisible = true

            }


        }
    }

    override fun onItemClick(t: SummaryAttendance, pos: Int, boolean: Boolean) {
        findNavController().navigate(R.id.showAttendanceFragment,Bundle().apply {
            putInt("monthID",t.monthID)
        })

    }


}