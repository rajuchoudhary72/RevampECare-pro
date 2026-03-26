package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.ebook.EBook
import com.app.ecarepro.core.domain.repository.EBookRepository
import com.app.ecarepro.core.network.EBookRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EBookRepositoryImpl @Inject constructor(
    private val dataSource: EBookRemoteDataSource,
) : EBookRepository {

    override fun getEBooks(query: String, mode: Int): Flow<Result<Pair<List<EBook>, String?>>> =
        asResultFlow { dataSource.getEBooks(query, mode) }

    override fun getOnlineCode(accessionNo: String): Flow<Result<String>> =
        asResultFlow { dataSource.getOnlineCode(accessionNo) }
}
