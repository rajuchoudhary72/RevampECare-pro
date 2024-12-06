package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Dtl
import com.app.ecarepro.model.MyReporting

data class NetworkLeaveReport(
    val dtl: List<Dtl>,
    val myReporting: List<MyReporting>,
    val errorCode: Int,
    val message: String,
    val status: String,
    val isRejectionReasonReq: Boolean,
    val canTalkeAction: Boolean =true
)