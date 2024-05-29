package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class AppLayoutDto(
    @SerializedName("additionalAccounts")
    val additionalAccounts: Any?,
    @SerializedName("erP_APPS")
    val erPAPPS: Any?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("isAuthenticated")
    val isAuthenticated: Boolean?,
    @SerializedName("logoURL")
    val logoURL: String?,
    @SerializedName("menus")
    val menus: List<Menu>?,
    @SerializedName("favoriteMenus")
    val favoriteMenus: List<Menu>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("smlLogoURL")
    val smlLogoURL: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("userInfo")
    val userInfo: UserInfo
)

data class Menu(
    @SerializedName("childMenus")
    val childMenus: List<ChildMenu>?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("menuID")
    val menuID: Int,
    @SerializedName("chMenuID")
    val chMenuID: Int,
    @SerializedName("sbChMenuID")
    val sbChMenuID: Int,
    @SerializedName("slNo")
    val slNo: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("url")
    val url: String?
)

data class UserInfo(
    @SerializedName("name")
    val name: String?,
    @SerializedName("otherInfo")
    val otherInfo: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("userID")
    val userID: Int?,
    @SerializedName("userType")
    val userType: Int?
)

data class ChildMenu(
    @SerializedName("chMenuID")
    val chMenuID: Int,
    @SerializedName("childMenus")
    val childMenus: List<ChildMenu>?,
    @SerializedName("sbChMenuID")
    val sbChMenuID: Int,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("menuID")
    val menuID: Int,
    @SerializedName("slNo")
    val slNo: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("url")
    val url: String?
)