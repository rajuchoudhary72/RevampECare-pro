package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName

data class SkillListDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("skillList")
    val skillList: List<Skill>?,
    @SerializedName("status")
    val status: String?
)

data class Skill(
    @SerializedName("category")
    val category: String?,
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("createdOn")
    val createdOn: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("modifiedBy")
    val modifiedBy: String?,
    @SerializedName("modifiedOn")
    val modifiedOn: String?,
    @SerializedName("skill")
    val skill: String?,
    @SerializedName("sklCatID")
    val sklCatID: Int?,
    @SerializedName("sklTypeID")
    val sklTypeID: Int?,
    @SerializedName("type")
    val type: String?
)


