package com.app.ecarepro.model

data class Library(
    val duesAMT: Double,
    val issued: Int,
    val libraryFineDTL: List<LibraryTransactionX>,
    val libraryTransaction: List<LibraryTransactionX>,
    val paidAMT: Double,
    val pending: Int,
    val pendingAMT: Double,
    val returned: Int,
    val waiveAMT: Double
)