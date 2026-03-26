package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.activity_calendar.NetworkActivityMonth

interface ActivityCalendarRemoteDataSource {
    suspend fun getActivityCalendar(): Pair<String, List<NetworkActivityMonth>>
}
