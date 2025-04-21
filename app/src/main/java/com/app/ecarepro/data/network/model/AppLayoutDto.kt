package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.app.ecarepro.model.AppLayout
import com.app.ecarepro.ui.views.menu.DrawerMenu


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
    val userInfo: UserInfo?,

    @SerializedName("searchOptions")
    val searchOptions: List<SearchOption>?,
    @SerializedName("notificationCount")
    val notificationCount: Int?,
    @SerializedName("unreadMessageCount")
    val unreadMessageCount: Int?
)
@Parcelize
data class SearchOption(
    @SerializedName("option")
    val option: String,
    @SerializedName("show")
    val show: Boolean
) : Parcelable

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
    @SerializedName("childName")
    val childName: String?,
    @SerializedName("otherInfo")
    val otherInfo: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("userID")
    val userID: Int?,
    @SerializedName("userType")
    val userType: Int?
){
    fun getFullName(): String? {
        return if (childName.isNullOrEmpty())
            name
        else
            "$name  \nP/O  $childName"
    }

    fun getFullHomeScreenName(): String? {
        return if (childName.isNullOrEmpty())
            name
        else
            "$name  \nP/O  $childName ($otherInfo)"
    }
}

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
    val slNo: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("url")
    val url: String?
)


fun AppLayoutDto.toAppLayout() = AppLayout(
    errorCode = this.errorCode,
    isAuthenticated = this.isAuthenticated,
    logoURL = this.logoURL,
    menus = this.menus?.map { it.toDrawerMenu() },
    message = this.message,
    smlLogoURL = this.smlLogoURL,
    status = this.status,
    userInfo = this.userInfo,
    additionalAccounts = this.additionalAccounts,
    erPAPPS = this.erPAPPS,
    favoriteMenus = this.menus,
    searchOptions = this.searchOptions,
    notificationCount = this.notificationCount,
    unreadMessageCount = this.unreadMessageCount
)


fun Menu.toDrawerMenu() =  DrawerMenu(
    slNo = this.slNo,
    title = this.title,
    url = this.url,
    icon = this.icon,
    menuID = menuID,
    chMenuID = chMenuID,
    sbChMenuID = sbChMenuID,
    childMenus = childMenus?.map { it.toDrawerMenu() }
)

fun ChildMenu.toDrawerMenu(maxDepth: Int = 10): DrawerMenu {
    if (maxDepth == 0) return DrawerMenu(
        slNo = slNo,
        title = title,
        url = url,
        icon = icon,
        menuID = menuID,
        chMenuID = chMenuID,
        sbChMenuID = sbChMenuID,
        childMenus = null
    )
    return DrawerMenu(
        slNo = slNo,
        title = title,
        url = url,
        icon = icon,
        menuID = menuID,
        chMenuID = chMenuID,
        sbChMenuID = sbChMenuID,
        childMenus = childMenus?.map { it.toDrawerMenu(maxDepth - 1) }
    )
}