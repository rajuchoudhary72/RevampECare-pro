package com.app.ecarepro.data.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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

@Parcelize
data class Skill(
    @SerializedName("category")
    val category: String?,
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("createdOn")
    val createdOn: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("modifiedBy")
    val modifiedBy: String?,
    @SerializedName("modifiedOn")
    val modifiedOn: String?,
    @SerializedName("skill")
    val skill: String?,
    @SerializedName("sklCatID")
    val sklCatID: Int,
    @SerializedName("sklTypeID")
    val sklTypeID: Int?,
    @SerializedName("type")
    val type: String?
) : Parcelable


