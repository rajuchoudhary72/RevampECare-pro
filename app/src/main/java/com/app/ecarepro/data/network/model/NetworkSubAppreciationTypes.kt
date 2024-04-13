package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.TypeAppreciation

data class NetworkSubAppreciationTypes(
    val errorCode: Int,
    val message: String,
    val status: String,
    val types: List<TypeAppreciation>
)