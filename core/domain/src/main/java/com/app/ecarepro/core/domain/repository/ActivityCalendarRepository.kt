package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.activity_calendar.ActivityMonth
import kotlinx.coroutines.flow.Flow

interface ActivityCalendarRepository {
    fun getActivityCalendar(): Flow<Result<Pair<String, List<ActivityMonth>>>>
}
