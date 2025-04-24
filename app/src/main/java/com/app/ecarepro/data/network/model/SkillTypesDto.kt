package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName

data class SkillTypesDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("types")
    val types: List<SkillType>?
)

data class SkillType(
    @SerializedName("skills")
    val skills: Any?,
    @SerializedName("sklTypeID")
    val sklTypeID: Int?,
    @SerializedName("type")
    val type: String?
)


