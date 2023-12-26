package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemOnlineVsOfflineAdmissionCardBinding
import com.app.ecarepro.databinding.ItemStaffAttendanceCardBinding
import com.app.ecarepro.databinding.ItemStanderWiseStatisticCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class OnlineVsOfflineAdmissionModel :
    ViewBindingKotlinModel<ItemOnlineVsOfflineAdmissionCardBinding>(R.layout.item_online_vs_offline_admission_card) {
    private var isExpanded = false
    override fun ItemOnlineVsOfflineAdmissionCardBinding.bind() {
        isExpanded = this@OnlineVsOfflineAdmissionModel.isExpanded
        title.setOnClickListener {
            this@OnlineVsOfflineAdmissionModel.isExpanded = this@OnlineVsOfflineAdmissionModel.isExpanded.not()
            chartView.isVisible = this@OnlineVsOfflineAdmissionModel.isExpanded
            groupCollapsed.isVisible = this@OnlineVsOfflineAdmissionModel.isExpanded.not()
        }

        chartView.aa_drawChartWithChartModel(getBarChartModel())
    }

    private fun getBarChartModel() = AAChartModel()
        .chartType(AAChartType.Pie)
        .dataLabelsEnabled(true) //是否直接显示扇形图数据
        .series(
            arrayOf(
                AASeriesElement()
                    .name("Past")
                    .size("80%") //尺寸大小
                    .innerSize("70%") //内部圆环半径大小占比
                    .borderWidth(0) //描边的宽度
                    .allowPointSelect(false) //是否允许在点击数据点标记(扇形图点击选中的块发生位移)
                    .data(
                        arrayOf(
                           arrayOf("Online",20),
                           arrayOf("Offline",80),
                        )
                    )
            )
        )

}