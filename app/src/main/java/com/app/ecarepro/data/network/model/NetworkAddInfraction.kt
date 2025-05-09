package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.InfractionConsequence
import com.app.ecarepro.model.InfractionType
import com.app.ecarepro.model.RecentInfraction
import com.app.ecarepro.model.Staff
import com.app.ecarepro.model.StudentDTL

data class NetworkAddInfraction(
    val errorCode: Int,
    val infractionConsequences: List<InfractionConsequence>,
    val infractionTypes: List<InfractionType>,
    val message: String,
    val recentInfractions: List<RecentInfraction>,
    val status: String,
    val studentDTL: StudentDTL,
    val stafftDTL: Staff
)