package com.app.ecarepro.data.network.model.post_save_infraction

data class PostSaveInfraction(
    val action: Int,
    val consID: Int,
    val correctiveAction: String,
    val infrSubTypeID: Int,
    val infractionOn: String,
    val instance: Int,
    val stID: Int
)