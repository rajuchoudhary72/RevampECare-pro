package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.ActivityCalendarRemoteDataSource
import com.app.ecarepro.core.network.model.activity_calendar.NetworkActivityMonth
import com.app.ecarepro.core.network.retrofit.service.AcademicService
import javax.inject.Inject

internal class ActivityCalendarRemoteDataSourceImpl @Inject constructor(
    private val academicService: AcademicService,
) : ActivityCalendarRemoteDataSource {

    override suspend fun getActivityCalendar(): Pair<String, List<NetworkActivityMonth>> {
        val response = academicService.getActivityCalendar()
        return (response.session.orEmpty()) to (response.activityMonth ?: emptyList())
    }
}
