package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkThoughts
import com.app.ecarepro.data.network.model.NetworkWhoLike
import com.app.ecarepro.model.Notice

interface ThoughtsRepo {

    suspend fun getThoughts(pg: Int,
                          dir: Int,
                           mythoughts: Boolean): NetworkThoughts

    suspend fun like( thID: Int,
                     like: Boolean): CommonResponse
    suspend fun thoughtsDelete( thID: Int ): CommonResponse

    suspend fun whoLiked( thID: Int ): NetworkWhoLike

    suspend fun thoughtsCreate (quotation:String,author:String): CommonResponse

}