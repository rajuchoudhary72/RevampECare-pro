package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.airbnb.epoxy.Carousel
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkFeeDefaulter
import com.app.ecarepro.data.network.model.Workload
import com.app.ecarepro.model.FeeDefaulter
import com.app.ecarepro.databinding.ItemFeeDefaulterCardBinding
import com.app.ecarepro.databinding.ItemTeacherWorkloadCardBinding
import com.app.ecarepro.feeDefaulterCard
import com.app.ecarepro.teacherClassOverloadCard
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.app.ecarepro.ui.views.subTitle
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import java.util.IllegalFormatConversionException

/*class FeeDefaulterModel(val feeDefaulter: FeeDefaulter?) :
    ViewBindingKotlinModel<ItemFeeDefaulterCardBinding>(R.layout.item_fee_defaulter_card) {*/


    class FeeDefaulterModel(val feeDefaulter: NetworkFeeDefaulter?, val onClick:() -> Unit) :
        ViewBindingKotlinModel<ItemFeeDefaulterCardBinding>(R.layout.item_fee_defaulter_card) {


    private var isExpanded = false

 /*       override fun ItemTeacherWorkloadCardBinding.bind() {
            carousel.numViewsToShowOnScreen = 1.6f
            carousel.setPadding(Carousel.Padding(0,15))
            carousel.withModels {
                workload.forEach {
                    feeDefaulterCard {
                        id(it.id)
                        FeeDefaulter(it)
                        clickListener { _ ->
                            onClick(it)
                        }
                    }
                }
            }
        }*/
    override fun ItemFeeDefaulterCardBinding.bind() {
      //  isExpanded = this@FeeDefaulterModel.isExpanded
        title.setOnClickListener {
            onClick()
          /*  this@FeeDefaulterModel.isExpanded = this@FeeDefaulterModel.isExpanded.not()
            chartView.isVisible = this@FeeDefaulterModel.isExpanded
            groupCollapsed.isVisible = this@FeeDefaulterModel.isExpanded.not()*/
        }

        /**/
        // Convert to BigDecimal to avoid scientific notation
        val number = feeDefaulter?.totalAmount
        if (feeDefaulter==null){
            val roundedNumber = String.format("%.2f", number)
            amount.subTitle("₹ 0 ")
            total.subTitle("0")
            defaulter.subTitle("0")
        }else{
            try {
                val roundedNumber = String.format("%.2f", number)
                amount.subTitle("₹" + roundedNumber)
                total.subTitle(feeDefaulter?.totalStudent.toString())
                defaulter.subTitle(feeDefaulter?.feeDefaulters.toString())
            }catch (e: IllegalFormatConversionException){
                e.printStackTrace()
            }

        }
     //   chartView.isClearBackgroundColor = true
      //  chartView.aa_drawChartWithChartModel(getBarChartModel(feeDefaulter))
    }

    private fun getBarChartModel(feeDefaulter: FeeDefaulter?) = AAChartModel()
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
                            arrayOf("Defaulter Amount ₹ 0.0 ", 29),
                        )
                    )
            )
        )

}