package com.app.ecarepro.core.network

import com.app.ecarepro.core.domain.model.library.LibraryBook
import com.app.ecarepro.core.domain.model.library.LibraryData

interface LibraryRemoteDataSource {
    suspend fun getLibraryData(): LibraryData
    suspend fun getBookDetail(bookId: Int): LibraryBook
    suspend fun searchBooks(query: String, page: Int): List<LibraryBook>
}
