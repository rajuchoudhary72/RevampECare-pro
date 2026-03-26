package com.app.ecarepro.core.network

import com.app.ecarepro.core.domain.model.ebook.EBook

interface EBookRemoteDataSource {
    suspend fun getEBooks(query: String, mode: Int): Pair<List<EBook>, String?>
    suspend fun getOnlineCode(accessionNo: String): String
}
