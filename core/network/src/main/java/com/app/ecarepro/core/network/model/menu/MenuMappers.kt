package com.app.ecarepro.core.network.model.menu

import com.app.ecarepro.core.domain.model.menu.MenuCategory
import com.app.ecarepro.core.domain.model.menu.MenuItem

fun NetworkMenuCategory.toDomainModel() = MenuCategory(
    id = categoryID,
    title = categoryName ?: "",
    iconUrl = icon,
    menuItems = menus.map { it.toDomainModel() },
)

fun NetworkMenuItem.toDomainModel(): MenuItem = MenuItem(
    id = menuID,
    parentMenuID = parentMenuID,
    title = title ?: "",
    url = url,
    iconUrl = icon,
    hasChildren = menus.isNotEmpty(),
    isNavigable = !url.isNullOrEmpty() && url != "javascript:void(0);",
    children = menus.map { it.toDomainModel() },
)
