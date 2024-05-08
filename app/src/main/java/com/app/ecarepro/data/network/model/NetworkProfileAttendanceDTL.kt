package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.ProfileAttendanceDTL

data class NetworkProfileAttendanceDTL(
    val errorCode: Int,
    val status: String,
    val message: String,
    val attDTL: ProfileAttendanceDTL,

    )