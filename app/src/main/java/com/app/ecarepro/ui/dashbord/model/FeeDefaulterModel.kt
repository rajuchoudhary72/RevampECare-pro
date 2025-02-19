package com.app.ecarepro.ui.dashbord.model

import android.annotation.SuppressLint
import android.util.Log
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkFeeDefaulter
import com.app.ecarepro.databinding.ItemFeeDefaulterCardBinding
import com.app.ecarepro.ui.views.epoxy.ViewBindingKotlinModel
import com.app.ecarepro.ui.views.subTitle
import java.util.IllegalFormatConversionException


class FeeDefaulterModel(val feeDefaulter: NetworkFeeDefaulter?, val onClick: () -> Unit) :
    ViewBindingKotlinModel<ItemFeeDefaulterCardBinding>(R.layout.item_fee_defaulter_card) {

    @SuppressLint("DefaultLocale")
    override fun ItemFeeDefaulterCardBinding.bind() {

        Log.e("HARI", "Module Build $feeDefaulter")

        title.setOnClickListener {
            onClick()
        }

        if (feeDefaulter == null) {
            amount.subTitle("₹ 0 ")
            total.subTitle("0")
            defaulter.subTitle("0")
        } else {
            amount.subTitle(feeDefaulter.totalAmount)
            total.subTitle(feeDefaulter.totalStudent.toString())
            defaulter.subTitle(feeDefaulter.totalDefaulter.toString())
        }
    }

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + feeDefaulter.hashCode()
        result = 31 * result + onClick.hashCode()
        return result
    }


}