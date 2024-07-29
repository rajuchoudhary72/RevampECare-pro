package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.StaffAttendance
import com.app.ecarepro.databinding.ItemStaffAttendanceCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class StaffAttendanceModel(val staffAttendance: StaffAttendance) :
    ViewBindingKotlinModel<ItemStaffAttendanceCardBinding>(R.layout.item_staff_attendance_card) {
    private var isExpanded = false
    override fun ItemStaffAttendanceCardBinding.bind() {
        isExpanded = this@StaffAttendanceModel.isExpanded
        title.setOnClickListener {
            this@StaffAttendanceModel.isExpanded = this@StaffAttendanceModel.isExpanded.not()
            chartView.isVisible = this@StaffAttendanceModel.isExpanded
            groupCollapsed.isVisible = this@StaffAttendanceModel.isExpanded.not()
        }

        lvPresent.setSubTitle(staffAttendance.present.toString())
        lvLeave.setSubTitle(staffAttendance.onLeave.toString())
        lvAbsent.setSubTitle(staffAttendance.absent.toString())
        chartView.isClearBackgroundColor = true

        chartView.aa_drawChartWithChartModel(getBarChartModel(staffAttendance))
    }

    private fun getBarChartModel(staffAttendance: StaffAttendance) = AAChartModel()
        .chartType(AAChartType.Pie)
        .dataLabelsEnabled(true) //是否直接显示扇形图数据
        .series(
            arrayOf(
                AASeriesElement()
                    .name("Staff Attendance")
                    .size("80%") //尺寸大小
                    .innerSize("70%") //内部圆环半径大小占比
                    .borderWidth(0) //描边的宽度
                    .allowPointSelect(false) //是否允许在点击数据点标记(扇形图点击选中的块发生位移)
                    .data(
                        arrayOf(
                            arrayOf("Present", staffAttendance.present),
                            arrayOf("Absent", staffAttendance.absent),
                            arrayOf("Leave", staffAttendance.onLeave)
                        )
                    )


            )
        )

}