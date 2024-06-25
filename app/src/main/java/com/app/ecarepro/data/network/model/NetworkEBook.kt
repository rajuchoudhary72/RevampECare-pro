package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Book

data class NetworkEBook(
    val books: List<Book>,
    val errorCode: Int,
    val megaBookLink: String,
    val message: String,
    val mode: Int,
    val query: Any,
    val status: String,
    val total: Int
)