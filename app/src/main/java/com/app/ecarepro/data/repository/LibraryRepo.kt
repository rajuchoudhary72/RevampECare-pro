package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkLatestBook

interface LibraryRepo {
    suspend fun getLibraryDTL( ): NetworkLatestBook
    suspend fun getBookDTL( bookID: Int,id: Int ): NetworkBookDetails
    suspend fun getLibrarySearch( query: String,pg: Int ): NetworkBookDetails
}