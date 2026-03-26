package com.app.ecarepro.core.domain.model.sms

data class DailyConsumptionItem(
    val sentOn: String,
    val displayDate: String,
    val displayDayOfWeek: String,
    val count: Int,
)
