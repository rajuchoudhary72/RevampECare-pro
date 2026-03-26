package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.activity_calendar.ActivityMonth
import com.app.ecarepro.core.domain.repository.ActivityCalendarRepository
import com.app.ecarepro.core.network.ActivityCalendarRemoteDataSource
import com.app.ecarepro.core.network.model.activity_calendar.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ActivityCalendarRepositoryImpl @Inject constructor(
    private val remoteDataSource: ActivityCalendarRemoteDataSource,
) : ActivityCalendarRepository {

    override fun getActivityCalendar(): Flow<Result<Pair<String, List<ActivityMonth>>>> {
        return asResultFlow {
            val (session, months) = remoteDataSource.getActivityCalendar()
            session to months.map { it.toDomainModel() }
        }
    }
}
