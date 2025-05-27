package com.app.ecarepro.data.network

import com.google.gson.annotations.SerializedName

data class SaveSkillDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("skill")
    val skill: String,
    @SerializedName("sklCatID")
    val sklCatID: String,
    @SerializedName("sklTypeID")
    val sklTypeID: String
)


data class SaveSkillTypeRequest(
    @SerializedName("sklTypeID")
    val sklTypeID: String? = null,
    @SerializedName("type")
    val type: String
)

data class SaveSkillCategoryRequest(
    @SerializedName("sklCatID")
    val sklCatID: String? = null,
    @SerializedName("category")
    val category: String
)
