package com.app.ecarepro.ui.dashbord.model

import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemEstimateCollectionCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class EstimateCollectionModel :
    ViewBindingKotlinModel<ItemEstimateCollectionCardBinding>(R.layout.item_estimate_collection_card) {
    override fun ItemEstimateCollectionCardBinding.bind() {
        val model = AAChartModel()
            .chartType(AAChartType.Bar)
            .dataLabelsEnabled(false)
            .margin(arrayOf(0,0,0,0))
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

        barChart.aa_drawChartWithChartModel(model)
    }
}