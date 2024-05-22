package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.defaulter_report_filter.Classess
import com.app.ecarepro.model.defaulter_report_filter.FeeType
import com.app.ecarepro.model.defaulter_report_filter.Installment
import com.app.ecarepro.model.defaulter_report_filter.School
import com.app.ecarepro.model.defaulter_report_filter.Section

data class DefaulterFilters(
    val schools : List<School>,
    val feetype : List<FeeType>,
    val classes : List<Classess>,
    val sections : List<Section>,
    val installment : List<Installment>
)
