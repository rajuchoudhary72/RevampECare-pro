package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Type

data class NetworkSubInfractionTypes(
    val errorCode: Int,
    val message: String,
    val status: String,
    val types: List<Type>
)