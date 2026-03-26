package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.model.library.LibraryBook
import com.app.ecarepro.core.domain.model.library.LibraryData
import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.repository.LibraryRepository
import com.app.ecarepro.core.network.LibraryRemoteDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    private val dataSource: LibraryRemoteDataSource,
) : LibraryRepository {

    override fun getLibraryData(): Flow<Result<LibraryData>> = asResultFlow {
        dataSource.getLibraryData()
    }

    override fun getBookDetail(bookId: Int): Flow<Result<LibraryBook>> = asResultFlow {
        dataSource.getBookDetail(bookId)
    }

    override suspend fun searchBooks(query: String, page: Int): Result<List<LibraryBook>> {
        return try {
            Result.success(dataSource.searchBooks(query, page))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
