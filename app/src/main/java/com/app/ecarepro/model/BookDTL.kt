package com.app.ecarepro.model

data class BookDTL(
    val accessionNumber: String,
    val author: String,
    val bookID: Int,
    val bookNo: String,
    val classNo: String,
    val coverImg: String,
    val eBookLink: String,
    val id: Any,
    val isIssuable: Int,
    val publicationYear: String,
    val storageHint: String,
    val subject: String,
    val title: String
)