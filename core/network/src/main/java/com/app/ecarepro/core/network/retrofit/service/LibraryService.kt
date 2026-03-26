package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.ebook.NetworkEBookOnlineCodeResponse
import com.app.ecarepro.core.network.model.ebook.NetworkEBookResponse
import com.app.ecarepro.core.network.model.library.NetworkLibraryBookDetailResponse
import com.app.ecarepro.core.network.model.library.NetworkLibraryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LibraryService {

    @GET("Library/DTL")
    suspend fun getLibraryData(): NetworkLibraryResponse

    @GET("Library/BookDTL")
    suspend fun getBookDetail(
        @Query("BookID") bookId: Int,
    ): NetworkLibraryBookDetailResponse

    @GET("Library/Search")
    suspend fun searchBooks(
        @Query("query") query: String,
        @Query("pg") page: Int,
    ): NetworkLibraryBookDetailResponse

    @GET("Library/eBooks")
    suspend fun getEBooks(
        @Query("query") query: String,
        @Query("mode") mode: Int,
    ): NetworkEBookResponse

    @GET("Library/OnlineCode")
    suspend fun getEBookOnlineCode(
        @Query("AccessionNo") accessionNo: String,
    ): NetworkEBookOnlineCodeResponse
}
