package com.app.ecarepro.data.network.model.post_save_infraction

import com.app.ecarepro.model.BrowsedFile

data class PostSaveInfraction(
    val uType:Int,
    val action: Int,
    val consID: Int,
    val correctiveAction: String,
    val infrSubTypeID: Int,
    val infractionOn: String,
    val instance: Int,
    val stID: Int,
    val consequencesAttachment: BrowsedFile?,
    val isComplianceActive:Boolean
)