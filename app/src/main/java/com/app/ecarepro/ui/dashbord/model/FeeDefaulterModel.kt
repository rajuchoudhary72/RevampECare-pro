package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.model.FeeDefaulter
import com.app.ecarepro.databinding.ItemFeeDefaulterCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.app.ecarepro.ui.views.subTitle
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class FeeDefaulterModel(val feeDefaulter: FeeDefaulter) :
    ViewBindingKotlinModel<ItemFeeDefaulterCardBinding>(R.layout.item_fee_defaulter_card) {
    private var isExpanded = false
    override fun ItemFeeDefaulterCardBinding.bind() {
        isExpanded = this@FeeDefaulterModel.isExpanded
        title.setOnClickListener {
            this@FeeDefaulterModel.isExpanded = this@FeeDefaulterModel.isExpanded.not()
            chartView.isVisible = this@FeeDefaulterModel.isExpanded
            groupCollapsed.isVisible = this@FeeDefaulterModel.isExpanded.not()
        }

        /**/
        // Convert to BigDecimal to avoid scientific notation
        val number = feeDefaulter.amount
        val roundedNumber = String.format("%.2f", number)
        amount.subTitle("₹" + roundedNumber)
        total.subTitle(feeDefaulter.totalStudent.toString())
        defaulter.subTitle(feeDefaulter.dafaulterCount.toString())
        chartView.isClearBackgroundColor = true
        chartView.aa_drawChartWithChartModel(getBarChartModel(feeDefaulter))
    }

    private fun getBarChartModel(feeDefaulter: FeeDefaulter) = AAChartModel()
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