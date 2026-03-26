package com.app.ecarepro.core.network.model.menu

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMenuResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String = "",
    @SerialName("categories") val categories: List<NetworkMenuCategory> = emptyList(),
) : NetworkResponse

@Serializable
data class NetworkMenuCategory(
    @SerialName("categoryID") val categoryID: Int,
    @SerialName("categoryName") val categoryName: String? = null,
    @SerialName("categoryOrder") val categoryOrder: Int? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("menus") val menus: List<NetworkMenuItem> = emptyList(),
    @SerialName("isSelected") val isSelected: Boolean? = null,
)

@Serializable
data class NetworkMenuItem(
    @SerialName("menuID") val menuID: Int,
    @SerialName("parentMenuID") val parentMenuID: Int? = null,
    @SerialName("slNo") val slNo: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("isBookmarked") val isBookmarked: Boolean? = null,
    @SerialName("isSelected") val isSelected: Boolean? = null,
    @SerialName("isModified") val isModified: Boolean? = null,
    @SerialName("isOverridden") val isOverridden: Boolean? = null,
    @SerialName("menus") val menus: List<NetworkMenuItem> = emptyList(),
)
