package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AppLayout
import com.app.ecarepro.ui.views.menu.DrawerMenu
import com.google.gson.annotations.SerializedName

data class LMSAppLayoutDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("isAuthenticated")
    val isAuthenticated: Boolean?,
    @SerializedName("logoURL")
    val logoURL: String?,
    @SerializedName("menus")
    val menus: List<LMSMenu>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("schoolName")
    val schoolName: String?,
    @SerializedName("showSettings")
    val showSettings: Boolean?,
    @SerializedName("smlLogoURL")
    val smlLogoURL: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("userInfo")
    val userInfo: UserInfo?
)

data class LMSMenu(
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("menuID")
    val menuID: Int,
    @SerializedName("menuName")
    val menuName: String?,
    @SerializedName("menuURL")
    val menuURL: Any?,
    @SerializedName("parentID")
    val parentID: Any?,
    @SerializedName("subMenus")
    val subMenus: List<LMSMenu>?
)


fun LMSAppLayoutDto.toAppLayout(): AppLayout {
    return AppLayout(
        errorCode = this.errorCode,
        isAuthenticated = this.isAuthenticated,
        logoURL = this.logoURL,
        menus = this.menus?.map { it.toMenu() },
        message = this.message,
        smlLogoURL = this.smlLogoURL,
        status = this.status,
        userInfo = this.userInfo,
        additionalAccounts = null,
        erPAPPS = null,
        favoriteMenus = null,
        searchOptions = null,
        notificationCount = 0,
        unreadMessageCount = 0

    )
}

private fun LMSMenu.toMenu(maxDepth: Int = 10): DrawerMenu {
    if (maxDepth == 0) {
        return DrawerMenu(
            menuID = menuID,
            chMenuID = 0,
            sbChMenuID = 0,
            slNo = 0,
            icon = icon,
            title = menuName,
            url = menuURL.toString(),
            childMenus = subMenus?.map { it.toMenu() }
        )
    }

    return DrawerMenu(
        menuID = menuID,
        chMenuID = 0,
        sbChMenuID = 0,
        slNo = 0,
        icon = icon,
        title = menuName,
        url = menuURL.toString(),
        childMenus = subMenus?.map { it.toMenu(maxDepth - 1) }
    )
}
