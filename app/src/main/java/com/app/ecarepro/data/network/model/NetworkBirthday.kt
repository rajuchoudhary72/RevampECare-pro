package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.UsersBirthday

data class NetworkBirthday(
    val errorCode: Int,
    val message: String,
    val status: String,
    val usersBirthday: List<UsersBirthday>
)