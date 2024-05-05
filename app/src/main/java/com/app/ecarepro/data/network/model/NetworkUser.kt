package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class NetworkUser(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)
