package com.app.ecarepro.data.network.model

import com.app.ecarepro.data.database.model.UserEntity
import com.google.gson.annotations.SerializedName


data class NetworkUser(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

fun NetworkUser.asEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name
    )
}