package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.MonthlyPaySlip

data class Year(
    val monthlyPaySlip: List<MonthlyPaySlip>,
    val year: Int
)