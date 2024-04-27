package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.RecentAppreciation
import com.app.ecarepro.model.StudentDTL

data class NetworkAppreciations(
    val errorCode: Int,
    val message: String,
    val records: List<RecentAppreciation>,
    val showPoints: Boolean,
    val status: String,
    val studentDTL: StudentDTL,
    val totalPoints: Int
)