package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Staff

data class NetworkStaffList(
    val errorCode: Int,
    val message: String,
    val staffs: List<Staff>,
    val status: String
)