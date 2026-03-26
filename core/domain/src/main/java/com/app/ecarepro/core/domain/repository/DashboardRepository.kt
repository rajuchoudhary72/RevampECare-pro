package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.dashboard.DashFeedItem
import com.app.ecarepro.core.domain.model.dashboard.DashboardResponse
import com.app.ecarepro.core.domain.model.dashboard.FeeCollectionData
import com.app.ecarepro.core.domain.model.dashboard.ModeWiseData
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDashboard(): Flow<Result<DashboardResponse>>
    fun getFeeCollection(feeTypeId: Int, fromDate: String, tillDate: String): Flow<Result<FeeCollectionData>>
    fun getModeWiseCollection(collectionDate: String): Flow<Result<ModeWiseData>>
    fun getFeed(): Flow<Result<List<DashFeedItem>>>
}
