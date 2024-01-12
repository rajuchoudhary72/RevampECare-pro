package com.app.ecarepro.data

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkThoughts
import com.app.ecarepro.data.network.model.NetworkWhoLike
import com.app.ecarepro.data.network.post_data.AddThoughtsPostData
import com.app.ecarepro.data.network.service.ThoughtsService
import com.app.ecarepro.data.repository.ThoughtsRepo
import javax.inject.Inject

class ThoughtsRepoImpl @Inject constructor(
    private val thoughtsService: ThoughtsService
) : ThoughtsRepo {
    override suspend fun getThoughts(pg: Int, dir: Int, mythoughts: Boolean): NetworkThoughts {
       return  thoughtsService.getThoughts(pg, dir, mythoughts)
    }

    override suspend fun like(thID: Int, like: Boolean): CommonResponse {
        return thoughtsService.like(thID, like)
    }

    override suspend fun thoughtsDelete(thID: Int): CommonResponse {
        return thoughtsService.thoughtsDelete(thID )
    }

    override suspend fun whoLiked(thID: Int): NetworkWhoLike {
        return  thoughtsService.whoLiked(thID)
    }

    override suspend fun thoughtsCreate(quotation: String, author: String): CommonResponse {
         return thoughtsService.thoughtsCreate(AddThoughtsPostData(quotation, author))
    }
}