package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.LoginResponseDto

import com.app.ecarepro.data.network.model.NetworkThoughts
import com.app.ecarepro.data.network.model.NetworkWhoLike
import com.app.ecarepro.data.network.post_data.AddThoughtsPostData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ThoughtsService {

    @Headers("Accept: application/json")
    @GET("Thoughts/List")
    suspend fun getThoughts(
        @Query("pg") pg: Int,
        @Query("dir") dir: Int,
        @Query("mythoughts") mythoughts: Boolean
    ): NetworkThoughts


    @Headers("Accept: application/json")
    @GET("Thoughts/Like")
    suspend fun like(
        @Query("ThID") thID: Int,
        @Query("Like") like: Boolean
    ): CommonResponse

    @Headers("Accept: application/json")
    @GET("Thoughts/WhoLiked")
    suspend fun whoLiked(
        @Query("ThID") thID: Int
    ): NetworkWhoLike

    @POST("Thoughts/Create")
    suspend fun thoughtsCreate(
        @Body request: AddThoughtsPostData,
    ): CommonResponse

    @Headers("Accept: application/json")
    @GET("Thoughts/Delete")
    suspend fun thoughtsDelete(
        @Query("ThID") thID: Int
    ): CommonResponse

}