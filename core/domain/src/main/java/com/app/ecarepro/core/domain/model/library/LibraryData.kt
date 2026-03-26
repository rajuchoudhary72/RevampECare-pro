package com.app.ecarepro.core.domain.model.library

data class LibraryData(
    val latestBooks: List<LibraryBook>,
    val myAccountBooks: List<LibraryBook>,
)
