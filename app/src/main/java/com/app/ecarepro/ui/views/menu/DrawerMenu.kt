package com.app.ecarepro.ui.views.menu


data class DrawerMenu(
    val slNo: Int,
    val title: String?,
    val url: String?,
    val icon: String?,
    val menuID: Int,
    val chMenuID: Int,
    val sbChMenuID: Int,
    val childMenus: List<DrawerMenu>?,
)