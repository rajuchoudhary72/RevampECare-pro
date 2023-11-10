package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.airbnb.epoxy.Carousel
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemFeedsCardBinding
import com.app.ecarepro.databinding.ItemStatudentStatusticCardBinding
import com.app.ecarepro.notificationCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class StudentStatisticModel :
    ViewBindingKotlinModel<ItemStatudentStatusticCardBinding>(R.layout.item_statudent_statustic_card) {
    private var isExpanded = false

    override fun ItemStatudentStatusticCardBinding.bind() {
        isExpanded = this@StudentStatisticModel.isExpanded
        title.setOnClickListener {
            this@StudentStatisticModel.isExpanded = this@StudentStatisticModel.isExpanded.not()
            chartView.isVisible = this@StudentStatisticModel.isExpanded
            groupCollapsed.isVisible = this@StudentStatisticModel.isExpanded.not()
        }

        chartView.aa_drawChartWithChartModel(getBarChartModel())
    }

    private fun getBarChartModel() = AAChartModel()
        .chartType(AAChartType.Pie)
        .dataLabelsEnabled(true) //是否直接显示扇形图数据
        .series(arrayOf(
            AASeriesElement()
                .name("Past")
                .size("80%") //尺寸大小
                .innerSize("70%") //内部圆环半径大小占比
                .borderWidth(0) //描边的宽度
                .allowPointSelect(false) //是否允许在点击数据点标记(扇形图点击选中的块发生位移)
                .data(arrayOf(
                    arrayOf("Hindu", 80.2),
                    arrayOf("Muslim ",    26.8),
                    arrayOf("Christian ",    26.8),
                )))
        )
}