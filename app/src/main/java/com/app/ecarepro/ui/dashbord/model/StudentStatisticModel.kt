package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.DataValue
import com.app.ecarepro.databinding.ItemStatudentStatusticCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class StudentStatisticModel(val data: List<DataValue>) :
    ViewBindingKotlinModel<ItemStatudentStatusticCardBinding>(R.layout.item_statudent_statustic_card) {
    private var isExpanded = false

    override fun ItemStatudentStatusticCardBinding.bind() {
        isExpanded = this@StudentStatisticModel.isExpanded
        title.setOnClickListener {
            this@StudentStatisticModel.isExpanded = this@StudentStatisticModel.isExpanded.not()
            chartView.isVisible = this@StudentStatisticModel.isExpanded
            groupCollapsed.isVisible = this@StudentStatisticModel.isExpanded.not()
        }

        lvHindu.setTitle(data.getOrNull(0)?.data ?: "")
        lvHindu.setSubTitle(data.getOrNull(0)?.value.toString())

        lvMuslim.setTitle(data.getOrNull(1)?.data ?: "")
        lvMuslim.setSubTitle(data.getOrNull(1)?.value.toString())

        lvChristian.setTitle(data.getOrNull(2)?.data ?: "")
        lvChristian.setSubTitle(data.getOrNull(2)?.value.toString())
        chartView.isClearBackgroundColor = true

        chartView.aa_drawChartWithChartModel(getBarChartModel(data))
    }

    private fun getBarChartModel(data: List<DataValue>) = AAChartModel()
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
                        data.map {
                            arrayOf(
                                it.data,
                                it.value
                            )
                        }.toTypedArray()
                    )
            )
        )
}