package com.app.ecarepro.data.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class SkillCategoriesDto(
    @SerializedName("categories")
    val categories: List<Category>?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

@Parcelize
data class Category(
    @SerializedName("category")
    val category: String?,
    @SerializedName("sklCatID")
    val sklCatID: Int,
    @SerializedName("types")
    val types: String?
) : Parcelable


