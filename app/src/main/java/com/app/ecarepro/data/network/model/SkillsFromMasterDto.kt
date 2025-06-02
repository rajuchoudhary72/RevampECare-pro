package com.app.ecarepro.data.network.model
import com.google.gson.annotations.SerializedName

data class SkillsFromMasterDto(
    @SerializedName("categories")
    val categories: List<MasterCategory>?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

data class MasterCategory(
    @SerializedName("category")
    val category: String?,
    @SerializedName("sklCatID")
    val sklCatID: Int?,
    @SerializedName("types")
    val types: List<Type>?
)

data class Type(
    @SerializedName("skills")
    val skills: List<TypeSkill>?,
    @SerializedName("sklTypeID")
    val sklTypeID: Int?,
    @SerializedName("type")
    val type: String?
)

data class TypeSkill(
    @SerializedName("skill")
    val skill: String?,
    @SerializedName("sklID")
    val sklID: Int?
)


