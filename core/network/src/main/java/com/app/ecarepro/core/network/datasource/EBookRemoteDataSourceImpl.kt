package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.domain.model.ebook.EBook
import com.app.ecarepro.core.network.EBookRemoteDataSource
import com.app.ecarepro.core.network.model.ebook.toDomainModel
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.LibraryService
import javax.inject.Inject

class EBookRemoteDataSourceImpl @Inject constructor(
    private val service: LibraryService,
) : EBookRemoteDataSource {

    override suspend fun getEBooks(query: String, mode: Int): Pair<List<EBook>, String?> {
        val response = service.getEBooks(query, mode).unwrapPayload { this }
        val books = response.books?.map { it.toDomainModel() } ?: emptyList()
        return books to response.megaBookLink
    }

    override suspend fun getOnlineCode(accessionNo: String): String {
        val response = service.getEBookOnlineCode(accessionNo).unwrapPayload { this }
        return response.message
    }
}
