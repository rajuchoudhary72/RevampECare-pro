package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemStaffAttendanceCardBinding
import com.app.ecarepro.databinding.ItemStanderWiseStatisticCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class StanderWiseStatisticModel :
    ViewBindingKotlinModel<ItemStanderWiseStatisticCardBinding>(R.layout.item_stander_wise_statistic_card) {
    private var isExpanded = false
    override fun ItemStanderWiseStatisticCardBinding.bind() {
        isExpanded = this@StanderWiseStatisticModel.isExpanded
        title.setOnClickListener {
            this@StanderWiseStatisticModel.isExpanded = this@StanderWiseStatisticModel.isExpanded.not()
            chartView.isVisible = this@StanderWiseStatisticModel.isExpanded
            groupCollapsed.isVisible = this@StanderWiseStatisticModel.isExpanded.not()
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
                            20, 30, 50, 40
                        )
                    )
            )
        )

}