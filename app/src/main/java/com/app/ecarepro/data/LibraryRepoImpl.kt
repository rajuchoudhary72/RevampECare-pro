package com.app.ecarepro.data

import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkLatestBook
import com.app.ecarepro.data.network.service.LibraryService
import com.app.ecarepro.data.repository.LibraryRepo
import javax.inject.Inject

class LibraryRepoImpl  @Inject constructor(
    private val libraryService: LibraryService
) : LibraryRepo {
    override suspend fun getLibraryDTL(): NetworkLatestBook {
        return libraryService.getLibraryDTL()
     }

    override suspend fun getBookDTL(bookID: Int, id: Int): NetworkBookDetails {
        return libraryService.getBookDTL(bookID, id)
    }

    override suspend fun getLibrarySearch(query: String, pg: Int): NetworkBookDetails {
       return   libraryService.getLibrarySearch(query, pg)
    }
}