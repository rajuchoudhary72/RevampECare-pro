package com.app.ecarepro.data.network.model.post_default_report

import com.app.ecarepro.model.defaulter_report_filter.Classess
import com.app.ecarepro.model.defaulter_report_filter.FeeType
import com.app.ecarepro.model.defaulter_report_filter.Installment
import com.app.ecarepro.model.defaulter_report_filter.School
import com.app.ecarepro.model.defaulter_report_filter.Section

data class DefaultReportBody(
    val senderid : String,
    val DateFrom : String,
    val DateTo : String,
    val schoolid : String,
    val feetypeid : String,
    val classid : String,
    val sectionid : String,
    val installid : String,

)
