package com.app.ecarepro.data.network.model

import com.app.ecarepro.utils.Constant.Companion.BASE_URL_COM
import com.google.gson.annotations.SerializedName


data class FavouritesDto(
    @SerializedName("allMenus")
    val allMenus: List<Favourites>?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

data class Favourites(
    @SerializedName("chMenuID")
    val chMenuID: Int?,
    @SerializedName("fvtID")
    val fvtID: Int?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("isModified")
    val isModified: Any?,
    @SerializedName("isSelected")
    val isSelected: Boolean?,
    @SerializedName("menuID")
    val menuID: Int?,
    @SerializedName("sbChMenuID")
    val sbChMenuID: Int?,
    @SerializedName("slNo")
    val slNo: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("url")
    val url: Any?
) {
    fun getImageUrl() =  icon
}
