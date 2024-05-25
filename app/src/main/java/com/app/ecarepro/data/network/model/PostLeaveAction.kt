package com.app.ecarepro.data.network.model

data class PostLeaveAction(
    val action: Int,
    val applType: Int,
    val forwardedTo: Int,
    val lvID: Int
)