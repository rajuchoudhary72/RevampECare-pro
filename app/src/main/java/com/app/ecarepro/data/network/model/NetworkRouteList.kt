package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.RouteLST

data class NetworkRouteList(
    val errorCode: Int,
    val message: String,
    val routeLST: List<RouteLST>,
    val status: String
)