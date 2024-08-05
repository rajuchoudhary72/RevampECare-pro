package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.FeeCollection
import com.app.ecarepro.databinding.ItemEstimateCollectionCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.app.ecarepro.utils.rupeeText
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class EstimateCollectionModel(val feeCollection: FeeCollection) :
    ViewBindingKotlinModel<ItemEstimateCollectionCardBinding>(R.layout.item_estimate_collection_card) {
    private var isExpanded = false
    override fun ItemEstimateCollectionCardBinding.bind() {
        isExpanded = this@EstimateCollectionModel.isExpanded
        title.setOnClickListener {
            this@EstimateCollectionModel.isExpanded = this@EstimateCollectionModel.isExpanded.not()
            groupExpanded.isVisible = this@EstimateCollectionModel.isExpanded
            groupCollapsed.isVisible = this@EstimateCollectionModel.isExpanded.not()
        }

        textEstimatedAmount.rupeeText(feeCollection.estimate)
        textReceivedAmount.rupeeText(feeCollection.received)
        textConcessionAmount.rupeeText(feeCollection.concession)
        textDueAmount.rupeeText(feeCollection.due)

        textExpReceivedAmount.rupeeText(feeCollection.received)
        textExpEstimatedAmount.rupeeText(feeCollection.estimate)
        textExpConcessionDue.rupeeText(feeCollection.due)
        textExpConcessionAmount.rupeeText(feeCollection.concession)

        barChart.isClearBackgroundColor = true
        lineChart.isClearBackgroundColor = true

        barChart.aa_drawChartWithChartModel(getBarChartModel(feeCollection))
        lineChart.aa_drawChartWithChartModel(getLineChartModel(feeCollection))
    }

    private fun getBarChartModel(feeCollection: FeeCollection) = AAChartModel()
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
                            feeCollection.estimate ?: 0.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadius(10)
                    .showInLegend(false)
                    .fillColor("#1993D9")
                    .data(
                        arrayOf(
                            feeCollection.received ?: 0.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadius(10)
                    .showInLegend(false)
                    .fillColor("#F35E76")
                    .data(
                        arrayOf(
                            feeCollection.concession ?: 0.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadius(10)
                    .showInLegend(false)
                    .fillColor("#9999CC")
                    .data(
                        arrayOf(
                            feeCollection.due ?: 0.0,
                        )
                    ),
            )
        )
        .xAxisVisible(false)
        .yAxisVisible(false)

    private fun getLineChartModel(feeCollection: FeeCollection) = AAChartModel()
        .chartType(AAChartType.Column)
        .dataLabelsEnabled(false)
        .legendEnabled(false)
        .series(
            feeCollection.installmentCollections?.map { fee ->
                AASeriesElement()
                    .borderRadiusTopLeft(10)
                    .borderRadiusTopRight(10)
                    .name(fee.installment)
                    .fillColor("#06BE7C")
                    .data(
                        arrayOf(
                            fee.estimate ?: 0.0,
                            fee.received ?: 0.0,
                            fee.concession ?: 0.0,
                            fee.due ?: 0.0,
                        )
                    )
            }?.toTypedArray() ?: emptyArray()
        )

}