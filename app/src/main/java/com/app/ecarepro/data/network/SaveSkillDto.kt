package com.app.ecarepro.data.network

import com.google.gson.annotations.SerializedName

data class SaveSkillDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("skill")
    val skill: String?,
    @SerializedName("sklCatID")
    val sklCatID: Int?,
    @SerializedName("sklTypeID")
    val sklTypeID: Int?
)



