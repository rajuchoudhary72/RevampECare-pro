package com.app.ecarepro.core.domain.model.ebook

data class EBook(
    val bookID: Int,
    val title: String,
    val author: String,
    val coverImg: String?,
    val accessionNo: String,
)
