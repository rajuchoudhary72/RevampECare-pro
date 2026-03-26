package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.library.LibraryBook
import com.app.ecarepro.core.domain.model.library.LibraryData
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getLibraryData(): Flow<Result<LibraryData>>
    fun getBookDetail(bookId: Int): Flow<Result<LibraryBook>>
    suspend fun searchBooks(query: String, page: Int): Result<List<LibraryBook>>
}
