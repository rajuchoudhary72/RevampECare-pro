package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemAdmissionComparisonCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class AdmissionComparisonModel :
    ViewBindingKotlinModel<ItemAdmissionComparisonCardBinding>(R.layout.item_admission_comparison_card) {
    private var isExpanded = false
    override fun ItemAdmissionComparisonCardBinding.bind() {
        isExpanded = this@AdmissionComparisonModel.isExpanded
        title.setOnClickListener {
            this@AdmissionComparisonModel.isExpanded = this@AdmissionComparisonModel.isExpanded.not()
            barchartView.isVisible = this@AdmissionComparisonModel.isExpanded
            columChartView.isVisible = this@AdmissionComparisonModel.isExpanded.not()
        }

        barchartView.aa_drawChartWithChartModel(getBarChartModel())
        columChartView.aa_drawChartWithChartModel(getLineChartModel())
    }


    private fun getBarChartModel() = AAChartModel()
        .chartType(AAChartType.Bar)
        .dataLabelsEnabled(false)
        .margin(arrayOf(0, 0, 0, 0))
        .legendEnabled(true)
        .tooltipEnabled(false)
        .series(
            arrayOf(
                AASeriesElement()
                    .borderRadius(10)
                    .fillColor("#06BE7C")
                    .data(
                        arrayOf(
                            4.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadius(10)
                    .fillColor("#1993D9")
                    .data(
                        arrayOf(
                            6.0,
                        )
                    ),
                AASeriesElement()
                    .borderRadius(10)
                    .fillColor("#1993D9")
                    .data(
                        arrayOf(
                            6.0,
                        )
                    ),
            )
        )
        .xAxisVisible(false)
        .yAxisVisible(false)
    private fun getLineChartModel() = AAChartModel()
        .chartType(AAChartType.Bar)
        .dataLabelsEnabled(false)
        .legendEnabled(true)
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
                    .borderWidth(0) //描
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