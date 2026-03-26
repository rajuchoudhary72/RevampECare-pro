package com.app.ecarepro.feature.fee.common

import java.text.NumberFormat
import java.util.Locale

fun formatAsCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    return "₹ ${formatter.format(amount)}"
}

fun String?.formatAsCurrency(): String = formatAsCurrency(this?.toDoubleOrNull() ?: 0.0)
