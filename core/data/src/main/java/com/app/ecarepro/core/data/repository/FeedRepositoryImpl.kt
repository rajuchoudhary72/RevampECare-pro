package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.FeedResponse
import com.app.ecarepro.core.domain.repository.FeedRepository
import com.app.ecarepro.core.network.SchoolRemoteDataSource
import com.app.ecarepro.core.network.model.feed.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FeedRepositoryImpl @Inject constructor(
    private val schoolRemoteDataSource: SchoolRemoteDataSource,
) : FeedRepository {

    override fun getFeeds(isDashboard: Boolean, page: Int): Flow<Result<FeedResponse>> {
        return asResultFlow {
            schoolRemoteDataSource.getFeed(isDashboard, page).toDomainModel()
        }
    }
}
