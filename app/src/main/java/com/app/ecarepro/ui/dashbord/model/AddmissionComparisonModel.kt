package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.AdmissionComparison
import com.app.ecarepro.databinding.ItemAdmissionComparisonCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class AdmissionComparisonModel(val admissionComparisonModel: AdmissionComparison) :
    ViewBindingKotlinModel<ItemAdmissionComparisonCardBinding>(R.layout.item_admission_comparison_card) {
    private var isExpanded = false
    override fun ItemAdmissionComparisonCardBinding.bind() {
        isExpanded = this@AdmissionComparisonModel.isExpanded
        title.setOnClickListener {
            this@AdmissionComparisonModel.isExpanded =
                this@AdmissionComparisonModel.isExpanded.not()
            barchartView.isVisible = this@AdmissionComparisonModel.isExpanded
            columChartView.isVisible = this@AdmissionComparisonModel.isExpanded.not()
        }
        barchartView.isClearBackgroundColor = true
        columChartView.isClearBackgroundColor = true

        barchartView.aa_drawChartWithChartModel(getBarChartModel(admissionComparisonModel))
        columChartView.aa_drawChartWithChartModel(getLineChartModel(admissionComparisonModel))
    }


    private fun getBarChartModel(admissionComparisonModel: AdmissionComparison) = AAChartModel()
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

    private fun getLineChartModel(admissionComparisonModel: AdmissionComparison) = AAChartModel()
        .chartType(AAChartType.Bar)
        .dataLabelsEnabled(false)
        .legendEnabled(true)
        .series(
            admissionComparisonModel.studentCountStandardWise?.map { data ->
                AASeriesElement()
                    .borderRadiusTopLeft(10)
                    .borderRadiusTopRight(10)
                    .name(data.standard)
                    .fillColor("#06BE7C")
                    .data(
                        arrayOf(
                            data.previousSession ?: 0,
                            data.currentSession ?: 0
                        )
                    )
            }?.toTypedArray() ?: emptyArray()

        )

}