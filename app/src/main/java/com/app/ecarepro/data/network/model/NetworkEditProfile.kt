package com.app.ecarepro.data.network.model

import com.app.ecarepro.ui.edit_profile.model.Profile

data class NetworkEditProfile(
    val errorCode: Int,
    val message: String,
    val profile: Profile,
    val status: String
)