package com.app.ecarepro.ui.studentProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentStudentProfileAttendanceBinding
import com.app.ecarepro.model.AttendanceDTL
import com.app.ecarepro.model.ProfileAttendanceDTL
import com.app.ecarepro.ui.timeTable.DayWiseListAdapter
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import kotlin.math.roundToInt


class StudentProfileAttendanceFragment(private val attendanceDTL: ProfileAttendanceDTL) : Fragment() {

    private lateinit var binding: FragmentStudentProfileAttendanceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_student_profile_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            try {
                tvPresentDay.text= buildString {
                    append("(")
                    append(setCalculatedPercentage(attendanceDTL.present, attendanceDTL.working))
                    append("%)")
                }
                tvAbsentDay.text= buildString {
                    append("(")
                    append(setCalculatedPercentage(attendanceDTL.absent, attendanceDTL.working))
                    append("%)")
                }
                tvLeaveDay.text= buildString {
                    append("(")
                    append(setCalculatedPercentage(attendanceDTL.leave, attendanceDTL.working))
                    append("%)")
                }
                tvLateDay.text= buildString {
                    append("(")
                    append(setCalculatedPercentage(attendanceDTL.late, attendanceDTL.working))
                    append("%)")
                }

                binding.pieChartView.aa_drawChartWithChartModel(getBarChartModel(
                    ((attendanceDTL.present * 100 / attendanceDTL.working * 100.0).roundToInt()) ,
                    ((attendanceDTL.leave * 100 / attendanceDTL.working * 100.0).roundToInt())  ,
                    ((attendanceDTL.absent * 100 / attendanceDTL.working * 100.0).roundToInt()) ,
                    ((attendanceDTL.late * 100 / attendanceDTL.working * 100.0).roundToInt() )
                ))
            }catch (_:Exception){ }

            tvTotalWorking.text= buildString { append(attendanceDTL.working) }
            tvTotalAbsent.text= buildString{ append(attendanceDTL.absent) }
            tvTotalPresent.text= buildString{ append(attendanceDTL.present) }
            tvTotalLate.text= buildString { append(attendanceDTL.late) }

            if (attendanceDTL.summaryAttendance!=null){
                val assignmentListAdapter =
                    StudentProfileAtteListAdapter(attendanceDTL.summaryAttendance,
                        this@StudentProfileAttendanceFragment)

                binding.rvAttendanceDetail.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = assignmentListAdapter
                }
                binding.rvAttendanceDetail.isVisible=true
                binding.tvNoData.isVisible=false


            }else{
                binding.rvAttendanceDetail.isVisible=false
                binding.tvNoData.isVisible=true

            }





        }

    }


    private fun setCalculatedPercentage(day: Int, totalDay: Int): String {
        return ((day * 100.00 / totalDay * 100.00).roundToInt() / 100.00).toString() + "%"
    }

    private fun getBarChartModel(present: Int,leave: Int,absent: Int,late: Int) = AAChartModel()

        .chartType(AAChartType.Pie)
        .dataLabelsEnabled(true)
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
                            arrayOf("Leave", leave),
                            arrayOf("Absent", absent),
                            arrayOf("Late", late)
                        )
                    )
            )
        )


}