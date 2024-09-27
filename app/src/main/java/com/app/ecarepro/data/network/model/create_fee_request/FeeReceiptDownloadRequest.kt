package com.app.ecarepro.data.network.model.create_fee_request

data class FeeReceiptDownloadRequest(
    val senderid : String,
    val stid : String,
    val recid : String,
    val feetypeid : String,
    val sessionid : Int,
 )
