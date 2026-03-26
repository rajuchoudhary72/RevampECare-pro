package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.FeedResponse
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeeds(isDashboard: Boolean, page: Int): Flow<Result<FeedResponse>>
}
