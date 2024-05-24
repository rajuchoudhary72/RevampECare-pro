package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.StatusWiseStatistics
import com.app.ecarepro.databinding.ItemStanderWiseStatisticCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class StanderWiseStatisticModel(val statusWiseStatistics: List<StatusWiseStatistics>) :
    ViewBindingKotlinModel<ItemStanderWiseStatisticCardBinding>(R.layout.item_stander_wise_statistic_card) {
    private var isExpanded = false
    override fun ItemStanderWiseStatisticCardBinding.bind() {
        isExpanded = this@StanderWiseStatisticModel.isExpanded
        title.setOnClickListener {
            this@StanderWiseStatisticModel.isExpanded =
                this@StanderWiseStatisticModel.isExpanded.not()
            chartView.isVisible = this@StanderWiseStatisticModel.isExpanded
            groupCollapsed.isVisible = this@StanderWiseStatisticModel.isExpanded.not()
        }

        lvLeft.setSubTitle(statusWiseStatistics.firstOrNull { it.data == "LEFT" }?.value.toString())
        lvTc.setSubTitle(statusWiseStatistics.firstOrNull { it.data == "TC" }?.value.toString())
        lvRepeater.setSubTitle(statusWiseStatistics.firstOrNull { it.data == "REPEATER" }?.value.toString())
        lvStudying.setSubTitle(statusWiseStatistics.firstOrNull { it.data == "STUDYING" }?.value.toString())

        chartView.aa_drawChartWithChartModel(getBarChartModel(statusWiseStatistics))
    }

    private fun getBarChartModel(statusWiseStatistics: List<StatusWiseStatistics>) = AAChartModel()
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
                        statusWiseStatistics.map {
                            arrayOf(it.data, it.value)
                        }.toTypedArray()
                    )
            )
        )

}