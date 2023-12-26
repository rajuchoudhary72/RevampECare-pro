package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemEstimateCollectionCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class EstimateCollectionModel :
    ViewBindingKotlinModel<ItemEstimateCollectionCardBinding>(R.layout.item_estimate_collection_card) {
        private var isExpanded = false
    override fun ItemEstimateCollectionCardBinding.bind() {
        isExpanded = this@EstimateCollectionModel.isExpanded
        title.setOnClickListener {
            this@EstimateCollectionModel.isExpanded = this@EstimateCollectionModel.isExpanded.not()
            groupExpanded.isVisible = this@EstimateCollectionModel.isExpanded
            groupCollapsed.isVisible = this@EstimateCollectionModel.isExpanded.not()
        }
        barChart.aa_drawChartWithChartModel(getBarChartModel())
        lineChart.aa_drawChartWithChartModel(getLineChartModel())
    }

    private fun getBarChartModel() = AAChartModel()
        .chartType(AAChartType.Bar)
        .dataLabelsEnabled(false)
        .margin(arrayOf(0, 0, 0, 0))
        .legendEnabled(false)
        .tooltipEnabled(false)
        .series(
            arrayOf(
                AASeriesElement()
                    .borderRadius(10)
                    .showInLegend(false)
                    .fillColor("#06BE7C")
                    .data(
                        arrayOf(
                            4.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadius(10)
                    .showInLegend(false)
                    .fillColor("#1993D9")
                    .data(
                        arrayOf(
                            6.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadius(10)
                    .showInLegend(false)
                    .fillColor("#F35E76")
                    .data(
                        arrayOf(
                            8.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadius(10)
                    .showInLegend(false)
                    .fillColor("#9999CC")
                    .data(
                        arrayOf(
                            9.5,
                        )
                    ),
            )
        )
        .xAxisVisible(false)
        .yAxisVisible(false)
    private fun getLineChartModel() = AAChartModel()
        .chartType(AAChartType.Column)
        .dataLabelsEnabled(false)
        .legendEnabled(false)
        .series(
            arrayOf(
                AASeriesElement()
                    .borderRadiusTopLeft(10)
                    .borderRadiusTopRight(10)
                    .name("Jan-Mar")
                    .fillColor("#06BE7C")
                    .data(
                        arrayOf(
                            4.0,
                            5.0,
                            6.0,
                            3.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadiusTopLeft(10)
                    .borderRadiusTopRight(10)
                    .fillColor("#1993D9")
                    .name("Apr-Jun")
                    .data(
                        arrayOf(
                            6.0,
                            4.0,
                            2.0,
                            9.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadiusTopLeft(10)
                    .borderRadiusTopRight(10)
                    .fillColor("#F35E76")
                    .name("Jul-Sep")
                    .data(
                        arrayOf(
                            8.0,
                            1.0,
                            4.0,
                            6.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadiusTopLeft(10)
                    .borderRadiusTopRight(10)
                    .fillColor("#9999CC")
                    .name("Oct-Dec")
                    .data(
                        arrayOf(
                            9.5,
                            5.5,
                            3.5,
                            8.5,
                        )
                    ),
            )
        )
}