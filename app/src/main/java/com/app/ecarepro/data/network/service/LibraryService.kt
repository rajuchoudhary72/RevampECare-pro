package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkLatestBook
import com.app.ecarepro.data.network.model.NetworkNotice
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface LibraryService {

    @Headers("Accept: application/json")
    @GET("Library/DTL")
    suspend fun getLibraryDTL(  ): NetworkLatestBook

    @Headers("Accept: application/json")
    @GET("Library/BookDTL")
    suspend fun getBookDTL(
        @Query("BookID") bookID: Int,
        @Query("ID") id: Int,
    ): NetworkBookDetails

    @Headers("Accept: application/json")
    @GET("Library/Search")
    suspend fun getLibrarySearch(
        @Query("query") query: String,
        @Query("pg") pg: Int,
    ): NetworkBookDetails


}