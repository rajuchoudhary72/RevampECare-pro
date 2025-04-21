package com.app.ecarepro.model

import com.app.ecarepro.data.network.model.SearchOption
import com.app.ecarepro.data.network.model.UserInfo
import com.app.ecarepro.data.network.model.Menu
import com.app.ecarepro.ui.views.menu.DrawerMenu


data class AppLayout(
    val additionalAccounts: Any?,
    val erPAPPS: Any?,
    val errorCode: Int?,
    val isAuthenticated: Boolean?,
    val logoURL: String?,
    val menus: List<DrawerMenu>?,
    val favoriteMenus: List<Menu>?,
    val message: String?,
    val smlLogoURL: String?,
    val status: String?,
    val userInfo: UserInfo?,
    val searchOptions: List<SearchOption>?,
    val notificationCount: Int?,
    val unreadMessageCount: Int?
)
