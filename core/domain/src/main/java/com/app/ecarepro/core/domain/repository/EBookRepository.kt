package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.ebook.EBook
import kotlinx.coroutines.flow.Flow

interface EBookRepository {
    fun getEBooks(query: String, mode: Int): Flow<Result<Pair<List<EBook>, String?>>>
    fun getOnlineCode(accessionNo: String): Flow<Result<String>>
}
