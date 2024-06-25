package com.app.ecarepro.data.network.model

data class MyAccount(
    val accessionNumber: String,
    val bookID: Int,
    val cost: String,
    val coverImg: String,
    val expReturnDate: String,
    val fineAmount: Double,
    val issueDate: String,
    val returnDate: String,
    val title: String
)