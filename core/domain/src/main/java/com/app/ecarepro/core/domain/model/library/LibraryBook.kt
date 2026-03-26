package com.app.ecarepro.core.domain.model.library

data class LibraryBook(
    val bookID: Int,
    val title: String,
    val author: String,
    val subject: String,
    val storageHint: String,
    val publicationYear: String,
    val bookNo: String,
    val classNo: String,
    val coverImg: String?,
    val status: String,
    val publication: String?,
    val cost: String?,
    val fineAmount: Double?,
    val isDelayed: Boolean,
    val issuable: String?,
    val accessionNumber: String,
    val issuedOn: String?,
    val expectedReturnDate: String?,
    val returnOn: String?,
)
