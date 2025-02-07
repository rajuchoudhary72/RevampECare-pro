package com.app.ecarepro.ui.dashbord.model

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.FeeCollection
import com.app.ecarepro.model.FeeType
import com.app.ecarepro.databinding.ItemEstimateCollectionCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.app.ecarepro.utils.rupeeText
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class EstimateCollectionModel(
    private val feeCollection: FeeCollection,
    private val isExpanded: Boolean = false,
    private val toggleCardVisibility: ((Boolean) -> Unit)? = null,
    private val updateFeeCollectionDate: ((feeTypeId: Int, dateFilterType: DateFilterType, isCalenderClick: Boolean) -> Unit)? = null,
) :
    ViewBindingKotlinModel<ItemEstimateCollectionCardBinding>(R.layout.item_estimate_collection_card) {

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun ItemEstimateCollectionCardBinding.bind() {
        setIsExpanded(this@EstimateCollectionModel.isExpanded)
        title.setOnClickListener {
            toggleCardVisibility?.invoke(this@EstimateCollectionModel.isExpanded.not())
        }

        /*groupExpanded.isVisible = this@EstimateCollectionModel.isExpanded
        groupCollapsed.isVisible = this@EstimateCollectionModel.isExpanded.not()*/

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

        btnCalender.setOnClickListener {
            updateFeeCollectionDate?.invoke(
                feeCollection.feeTypes?.firstOrNull { it.feeTypeName == editTextFeeType.text.toString() }?.feeTypeID!!,
                DateFilterType.fromString(editTextDate.text.toString()),
                true
            )
        }

        setupAutoCompleteTextView(editTextFeeType, feeCollection.feeTypes) {
            updateFeeCollectionDate?.invoke(
                it.feeTypeID,
                DateFilterType.fromString(editTextDate.text.toString()),
                false
            )
        }

        /*  editTextFeeType.setOnItemClickListener { parent, view, position, id ->
              val selectedItem =
                  parent.getItemAtPosition(position) as String // Assuming your items are strings
              updateFeeCollectionDate?.invoke(
                  FeeFilterType.fromString(selectedItem).id,
                  DateFilterType.fromString(editTextDate.text.toString()),
                  false
              )
          }*/
        editTextDate.setOnItemClickListener { parent, view, position, id ->
            updateFeeCollectionDate?.invoke(
                feeCollection.feeTypes?.firstOrNull { it.feeTypeName == editTextFeeType.text.toString() }?.feeTypeID!!,
                DateFilterType.fromString(editTextDate.text.toString()),
                false
            )
        }


        barChart.aa_drawChartWithChartModel(getBarChartModel(feeCollection))
        lineChart.aa_drawChartWithChartModel(getLineChartModel(feeCollection))
    }

    private fun setupAutoCompleteTextView(
        autoCompleteTextView: AutoCompleteTextView,
        items: List<FeeType>?,
        onItemSelect: (FeeType) -> Unit
    ) {
        val adapter = ArrayAdapter(
            autoCompleteTextView.context,
            android.R.layout.simple_dropdown_item_1line,
            items?.map { it.feeTypeName ?: "" } ?: emptyList()
        )
        autoCompleteTextView.setAdapter(adapter)

        if (autoCompleteTextView.text.isNullOrEmpty()) {
            autoCompleteTextView.setText(items?.firstOrNull()?.feeTypeName, false)
        }

        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            val selectedFeeType = items?.firstOrNull { it.feeTypeName == selectedItem }
            if (selectedFeeType != null) {
                onItemSelect.invoke(selectedFeeType)
            }
        }
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

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + feeCollection.hashCode()
        result = 31 * result + isExpanded.hashCode()
        result = 31 * result + (toggleCardVisibility?.hashCode() ?: 0)
        result = 31 * result + (updateFeeCollectionDate?.hashCode() ?: 0)
        return result
    }


}

enum class DateFilterType(val text: String) {
    TODAY("Till Today"),
    THIS_YEAR("This Year");

    companion object {
        fun fromString(text: String): DateFilterType {
            return values().firstOrNull { it.text == text } ?: TODAY
        }
    }
}