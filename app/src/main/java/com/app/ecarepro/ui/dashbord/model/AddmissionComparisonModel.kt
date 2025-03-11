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
        .margin(arrayOf(0, 0, 40, 2))
        .legendEnabled(true)
        .tooltipEnabled(true)
        .categories(
            arrayOf(
                admissionComparisonModel.previousSession.orEmpty(),
                admissionComparisonModel.currentSession.orEmpty(),
                admissionComparisonModel.nextSession.orEmpty()
            )
        )
        .series(
            arrayOf(
                AASeriesElement()
                    .borderRadius(10)
                    .fillColor(R.color.android_prvious_color)
                    .name(admissionComparisonModel.previousSession)
                    .data(
                        arrayOf(
                            admissionComparisonModel.studentCountStandardWise?.sumOf {
                                it.previousSession ?: 0
                            } ?: 0,
                        )
                    ),
                AASeriesElement()
                    .borderRadius(10)
                    .fillColor(R.color.android_current_color)
                    .name(admissionComparisonModel.currentSession)
                    .data(
                        arrayOf(
                            admissionComparisonModel.studentCountStandardWise?.sumOf {
                                it.currentSession ?: 0
                            } ?: 0,
                        )
                    ),
                AASeriesElement()
                    .borderRadius(10)
                    .fillColor(R.color.android_next_color)
                    .name(admissionComparisonModel.nextSession)
                    .data(
                        arrayOf(
                            admissionComparisonModel.studentCountStandardWise?.sumOf {
                                it.nextSession ?: 0
                            } ?: 0,
                        )
                    ),
            )
        )
        .xAxisVisible(false)
        .yAxisVisible(false)

    private fun getLineChartModel(admissionComparisonModel: AdmissionComparison) = AAChartModel()
        .chartType(AAChartType.Column)
        .dataLabelsEnabled(false)
        .legendEnabled(true)
        .categories(admissionComparisonModel.studentCountStandardWise?.map { it.standard.orEmpty() }
            ?.toTypedArray() ?: emptyArray())
        .series(
            arrayOf(
                AASeriesElement()
                    .borderRadiusTopLeft(10)
                    .borderRadiusTopRight(10)
                    .name(admissionComparisonModel.previousSession)
                    .fillColor(R.color.android_prvious_color)
                    .data(
                        admissionComparisonModel.studentCountStandardWise?.map {
                            it.previousSession ?: 0
                        }?.toTypedArray() ?: emptyArray()
                    ), AASeriesElement()
                    .borderRadiusTopLeft(10)
                    .borderRadiusTopRight(10)
                    .name(admissionComparisonModel.currentSession)
                    .fillColor(R.color.android_current_color)
                    .data(
                        admissionComparisonModel.studentCountStandardWise?.map {
                            it.currentSession ?: 0
                        }?.toTypedArray() ?: emptyArray()
                    ), AASeriesElement()
                    .borderRadiusTopLeft(10)
                    .borderRadiusTopRight(10)
                    .name(admissionComparisonModel.nextSession)
                    .fillColor(R.color.android_next_color)
                    .data(
                        admissionComparisonModel.studentCountStandardWise?.map {
                            it.nextSession ?: 0
                        }?.toTypedArray() ?: emptyArray()
                    )
            )

        )

}