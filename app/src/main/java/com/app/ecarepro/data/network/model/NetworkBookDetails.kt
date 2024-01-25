package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.BookDTL

data class NetworkBookDetails(
    val bookDTL: List<BookDTL>,
    val errorCode: Int,
    val message: String,
    val status: String
)