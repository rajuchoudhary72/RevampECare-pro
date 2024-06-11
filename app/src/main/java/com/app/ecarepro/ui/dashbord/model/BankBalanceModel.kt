package com.app.ecarepro.ui.dashbord.model

import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.BankBalance
import com.app.ecarepro.databinding.ItemBankBalanceCardBinding
import com.app.ecarepro.ui.views.LegendViewModel_
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class BankBalanceModel(val bankBalance: List<BankBalance>) :
    ViewBindingKotlinModel<ItemBankBalanceCardBinding>(R.layout.item_bank_balance_card) {
    private var isExpanded = false
    override fun ItemBankBalanceCardBinding.bind() {
        isExpanded = this@BankBalanceModel.isExpanded
        title.setOnClickListener {
            this@BankBalanceModel.isExpanded = this@BankBalanceModel.isExpanded.not()
            chartView.isVisible = this@BankBalanceModel.isExpanded
            groupCollapsed.isVisible = this@BankBalanceModel.isExpanded.not()
        }

        banks.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            withModels {
                bankBalance.forEach {
                    add(
                        LegendViewModel_()
                            .id(it.accountName)
                            .title(it.accountName!!)
                            .subTitle("₹${it.balnce}")
                            .image(null)
                    )
                }
            }
        }
        chartView.aa_drawChartWithChartModel(getBarChartModel(bankBalance))
    }

    private fun getBarChartModel(bankBalance: List<BankBalance>) = AAChartModel()
        .chartType(AAChartType.Pie)
        .dataLabelsEnabled(true) //是否直接显示扇形图数据
        .series(
            arrayOf(
                AASeriesElement()
                    .name("Bank Balance")
                    .size("80%") //尺寸大小
                    .innerSize("70%") //内部圆环半径大小占比
                    .borderWidth(0) //描边的宽度
                    .allowPointSelect(false) //是否允许在点击数据点标记(扇形图点击选中的块发生位移)
                    .data(
                        bankBalance.map {
                            arrayOf(it.accountName, it.balnce?.toDouble() ?: 0.0)
                        }.toTypedArray()

                    )
            )
        )


}