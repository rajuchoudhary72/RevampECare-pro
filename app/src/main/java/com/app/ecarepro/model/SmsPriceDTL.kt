package com.app.ecarepro.model

data class SmsPriceDTL(
    val affectedForm: String,
    val price: Double,
    val taxInPer: Int,
    val taxType: String
)