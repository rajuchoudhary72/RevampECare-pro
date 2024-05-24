package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.FeeReceipt
import com.app.ecarepro.model.FeeReceiptSession

data class NetworkFeeReceipt(

    val session_data: List<FeeReceiptSession>,
    val receipt_data: List<FeeReceipt>,
    val bytedata : String

)
