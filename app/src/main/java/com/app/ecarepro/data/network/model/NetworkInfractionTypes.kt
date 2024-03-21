package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Type

data class NetworkInfractionTypes(
    val errorCode: Int,
    val message: String,
    val showPoints: Boolean,
    val status: String,
    val types: List<Type>
)