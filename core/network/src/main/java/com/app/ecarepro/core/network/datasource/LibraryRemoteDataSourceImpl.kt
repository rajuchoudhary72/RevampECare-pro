package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.domain.model.library.LibraryBook
import com.app.ecarepro.core.domain.model.library.LibraryData
import com.app.ecarepro.core.network.LibraryRemoteDataSource
import com.app.ecarepro.core.network.model.library.toDomainModel
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.LibraryService
import javax.inject.Inject

class LibraryRemoteDataSourceImpl @Inject constructor(
    private val service: LibraryService,
) : LibraryRemoteDataSource {

    override suspend fun getLibraryData(): LibraryData {
        val response = service.getLibraryData().unwrapPayload { this }
        return LibraryData(
            latestBooks = response.latestBook?.map { it.toDomainModel() } ?: emptyList(),
            myAccountBooks = response.myAccount?.map { it.toDomainModel() } ?: emptyList(),
        )
    }

    override suspend fun getBookDetail(bookId: Int): LibraryBook {
        val response = service.getBookDetail(bookId).unwrapPayload { this }
        return response.bookDTL?.firstOrNull()?.toDomainModel()
            ?: throw IllegalStateException("Book not found")
    }

    override suspend fun searchBooks(query: String, page: Int): List<LibraryBook> {
        val response = service.searchBooks(query, page).unwrapPayload { this }
        return response.bookDTL?.map { it.toDomainModel() } ?: emptyList()
    }
}
