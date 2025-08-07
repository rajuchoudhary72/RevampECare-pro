package com.app.ecarepro.ui.studentProfile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentStudentProfileAttendanceBinding
import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.ProfileAttendanceDTL
import com.app.ecarepro.model.SummaryAttendance
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.TryAttendanceTest2
import com.app.ecarepro.ui.circuler.PopUpListAdapter
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile
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
class StudentProfileAttendanceFragment(

) : Fragment(), ItemListener<SummaryAttendance> {

    private var isYearSelected: Boolean = false
    private lateinit var selectedYearData: AcademicYear
    private lateinit var binding: FragmentStudentProfileAttendanceBinding
    private val studentProfileAttendanceViewModel: StudentProfileAttendanceViewModel by viewModels()
    private val sharedViewModel: SharedViewModelProfile by activityViewModels()

    private var attendanceDTL: ProfileAttendanceDTL? = null
    private var academicYears: List<AcademicYear>? = null
    private var studentID: Int = 0
    private var id: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            studentID = it.getInt(STUDENT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentProfileAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.getNetworkStudentProfile().observe(this.viewLifecycleOwner) {

            attendanceDTL = it.attendanceDTL
            academicYears = it.academicYears
            id = it.id


            if (attendanceDTL != null) {
                setupUi(attendanceDTL!!)

            }
            if (academicYears != null) {
                if (academicYears!!.isNotEmpty()) {
                    for (i in academicYears!!) {
                        if (i.isCur) {
                            binding.ctvSelectYear.text = i.session
                            break
                        }
                    }
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

    private fun getBarChartModel(
        present: Int,
        leave: Int,
        absent: Int,
        late: Int,
        toInt: Int,
        toInt1: Int
    ) = AAChartModel()

        .chartType(AAChartType.Pie)

        .colorsTheme(

            arrayOf("#4DAC3C", "#FF352F", "#FFD700", "#FFFEA11C")
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
        tvHeading.text = getString(R.string.select_academic_year)
        builder.setView(view)

        relOk.setOnClickListener {
            if (isYearSelected) {
                binding.ctvSelectYear.text = selectedYearData.session
                getAtt()
                builder.dismiss()
            }

        }

        val yearAdapter = PopUpListAdapter(academicYears, object : ItemListener<AcademicYear> {
            override fun onItemClick(t: AcademicYear, pos: Int, boolean: Boolean) {
                isYearSelected = true
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
        studentProfileAttendanceViewModel.getSAttendanceYrID(studentID, selectedYearData.yrID)

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


    private fun setupUi(attendanceDTL: ProfileAttendanceDTL) {

        with(binding) {

            ctvSelectYear.setOnClickListener {
                popUpSelectAcademicYears()
            }

            try {
                tvPresentDay.text = buildString {
                    append("(")
                    append(
                        setCalculatedPercentageToInt(
                            attendanceDTL.present,
                            attendanceDTL.working
                        )
                    )
                    append("%)")
                }
                tvAbsentDay.text = buildString {
                    append("(")
                    append(
                        setCalculatedPercentageToInt(
                            attendanceDTL.absent,
                            attendanceDTL.working
                        )
                    )
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
                tvWhDay.text = buildString {
                    append("(")
                    append(setCalculatedPercentageToInt(attendanceDTL.wh, attendanceDTL.working))
                    append("%)")
                }
                tvpresentWhDay.text = buildString {
                    append("(")
                    append(setCalculatedPercentageToInt(attendanceDTL.totalPresent, attendanceDTL.working))
                    append("%)")
                }

                binding.llLate.isVisible = attendanceDTL.isLateEnabled

                showPieChart(
                    attendanceDTL.isLateEnabled,
                    attendanceDTL.present,
                    attendanceDTL.absent,
                    attendanceDTL.leave,
                    attendanceDTL.late,
                    attendanceDTL.wh,
                    attendanceDTL.totalPresent
                )


                binding.pieChartView.isClearBackgroundColor = true
                binding.pieChartView.aa_drawChartWithChartModel(
                    getBarChartModel(
                        setCalculatedPercentageToInt(
                            attendanceDTL.present,
                            attendanceDTL.working
                        ).toInt(),
                        setCalculatedPercentageToInt(
                            attendanceDTL.leave,
                            attendanceDTL.working
                        ).toInt(),
                        setCalculatedPercentageToInt(
                            attendanceDTL.absent,
                            attendanceDTL.working
                        ).toInt(),
                        setCalculatedPercentageToInt(
                            attendanceDTL.late,
                            attendanceDTL.working
                        ).toInt(),
                        setCalculatedPercentageToInt(
                            attendanceDTL.wh,
                            attendanceDTL.working
                        ).toInt(),
                        setCalculatedPercentageToInt(
                            attendanceDTL.totalPresent,
                            attendanceDTL.working
                        ).toInt()
                    )

                )


            } catch (_: Exception) {
            }

            tvTotalWorking.text = buildString { append(attendanceDTL.working) }
            tvTotalAbsent.text = buildString { append(attendanceDTL.absent) }
            tvTotalPresent.text = buildString { append(attendanceDTL.present) }
            tvTotalLate.text = buildString { append(attendanceDTL.late) }
            tvTotalLeave.text = buildString { append(attendanceDTL.leave) }
            tvTotalWh.text = buildString { append(attendanceDTL.wh) }
            tvTotalPWh.text = buildString { append(attendanceDTL.totalPresent) }

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
        val intent = Intent(requireActivity(), TryAttendanceTest2::class.java)
        val bundle = Bundle()
        bundle.putString("studentID", id)
        bundle.putString("formDate", t.startDate)
        bundle.putString("tillDate", t.endDate)
        intent.putExtras(bundle)
        startActivity(intent)
        studentProfileAttendanceViewModel.sendScreenEvent()
        /* findNavController().navigate(R.id.showAttendanceFragment,Bundle().apply {
             putString("studentID",id)
             putString("formDate",t.startDate)
             putString("tillDate",t.endDate)
           })*/

    }

    private fun showPieChart(
        isLate: Boolean,
        totalPresent: Int,
        totalAbsent: Int,
        totalLeave: Int,
        totalLate: Int,
        workingHoliday: Int,
        workingPresentHoliday: Int
    ) {
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.setUsePercentValues(false)
        binding.pieChart.setRotationEnabled(false)
        binding.pieChart.setDrawMarkerViews(false)
        val yvalues = ArrayList<PieEntry>()
        if (!isLate) {
            yvalues.add(PieEntry(totalPresent.toFloat(), 0))
            yvalues.add(PieEntry(totalAbsent.toFloat(), 1))
            yvalues.add(PieEntry(totalLeave.toFloat(), 2))
            yvalues.add(PieEntry(workingHoliday.toFloat(), 4))
            yvalues.add(PieEntry(workingPresentHoliday.toFloat(), 5))
        } else {
            yvalues.add(PieEntry((totalPresent - totalLate).toFloat(), 0))
            yvalues.add(PieEntry(totalAbsent.toFloat(), 1))
            yvalues.add(PieEntry(totalLeave.toFloat(), 2))
            yvalues.add(PieEntry(totalLate.toFloat(), 3))
            yvalues.add(PieEntry(workingHoliday.toFloat(), 4))
            yvalues.add(PieEntry(workingPresentHoliday.toFloat(), 5))
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
            resources.getColor(R.color.category7),
            resources.getColor(R.color.present_wh),
        )
        else dataSet.setColors(
            resources.getColor(R.color.disabled),
            resources.getColor(R.color.absent_red),
            resources.getColor(R.color.att_leave_color),
            resources.getColor(R.color.att_late_color),
            resources.getColor(R.color.category7),
            resources.getColor(R.color.present_wh),
        )

        data.setValueTextSize(13f)
        data.setDrawValues(false)
        binding.pieChart.getLegend().setEnabled(false)
        binding.pieChart.animateXY(1400, 1400)

//        val s = """
//            ${totalPresent + totalAbsent + totalLeave}
//            Student(s)
//            """.trimIndent()
//        val length = (totalPresent + totalAbsent + totalLeave).toString() + ""
//        val ss1 = SpannableString(s)
//        ss1.setSpan(RelativeSizeSpan(2f), 0, length.length, 0) // set size
//        ss1.setSpan(
//            ForegroundColorSpan(resources.getColor(R.color.deep_black)),
//            0,
//            3,
//            0
//        ) // set color
//
//        binding.pieChart.centerText = ss1
        binding.pieChart.setCenterTextSize(16f)
        binding.pieChart.setCenterTextColor(resources.getColor(R.color.deep_black))
        binding.pieChart.holeRadius = 70f
        binding.pieChart.description = null
    }


    companion object {
        private const val STUDENT_ID = "student_id_int"

        fun newInstance(studentID: Int) = StudentProfileAttendanceFragment().apply {
            arguments = Bundle().apply {
                putInt(STUDENT_ID, studentID)
            }
        }

    }


}