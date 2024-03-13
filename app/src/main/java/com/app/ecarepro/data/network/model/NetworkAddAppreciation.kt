package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AppreciationReward
import com.app.ecarepro.model.AppreciationType
import com.app.ecarepro.model.RecentAppreciation
import com.app.ecarepro.model.StudentDTL

data class NetworkAddAppreciation(
    val appreciationRewards: List<AppreciationReward>,
    val appreciationTypes: List<AppreciationType>,
    val errorCode: Int,
    val message: String,
    val recentAppreciations: List<RecentAppreciation>,
    val status: String,
    val studentDTL: StudentDTL
)