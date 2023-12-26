package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemFeeDefaulterCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class FeeDefaulterModel :
    ViewBindingKotlinModel<ItemFeeDefaulterCardBinding>(R.layout.item_fee_defaulter_card) {
    private var isExpanded = false
    override fun ItemFeeDefaulterCardBinding.bind() {
        isExpanded = this@FeeDefaulterModel.isExpanded
        title.setOnClickListener {
            this@FeeDefaulterModel.isExpanded = this@FeeDefaulterModel.isExpanded.not()
            chartView.isVisible = this@FeeDefaulterModel.isExpanded
            groupCollapsed.isVisible = this@FeeDefaulterModel.isExpanded.not()
        }

        chartView.aa_drawChartWithChartModel(getBarChartModel())
    }

    private fun getBarChartModel() = AAChartModel()
        .chartType(AAChartType.Pie)
        .colorsTheme(arrayOf("#0c9674", "#7dffc0"))
        .dataLabelsEnabled(true)
        .yAxisTitle("℃")
        .legendEnabled(false)
        .series(
            arrayOf(
                AASeriesElement()
                    .name("Language market shares")
                    .data(
                        arrayOf(
                            arrayOf( 80),
                            arrayOf("Defaulter 885Amount ₹1,62,01,020", 29),
                        )
                    )
            )
        )

}