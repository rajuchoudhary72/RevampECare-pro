package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.RecentInfraction
import com.app.ecarepro.model.StudentDTL

data class NetworkInfractions(
    val errorCode: Int,
    val message: String,
    val records: List<RecentInfraction>,
    val showPoints: Boolean,
    val status: String,
    val studentDTL: StudentDTL,
    val totalPoints: Int
)