package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.ClassmateLST

data class NetworkClassmateLST(
    val classmateLST: List<ClassmateLST>,
    val errorCode: Int,
    val message: String,
    val status: String
)