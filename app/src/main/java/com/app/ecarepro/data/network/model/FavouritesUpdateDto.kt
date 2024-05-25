package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName

data class FavouritesUpdateDto(
    @SerializedName("chMenuID")
    val chMenuID: Int?,
    @SerializedName("fvtID")
    val fvtID: Int?,
    @SerializedName("isModified")
    val isModified: Boolean?,
    @SerializedName("isSelected")
    val isSelected: Boolean?,
    @SerializedName("menuID")
    val menuID: Int?,
    @SerializedName("sbChMenuID")
    val sbChMenuID: Int?,
    @SerializedName("slNo")
    val slNo: Int?
)