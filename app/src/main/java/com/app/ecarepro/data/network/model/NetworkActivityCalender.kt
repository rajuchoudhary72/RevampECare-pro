package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.ActivityMonth

data class NetworkActivityCalender(
    val activityMonth: List<ActivityMonth>,
    val session: String
)