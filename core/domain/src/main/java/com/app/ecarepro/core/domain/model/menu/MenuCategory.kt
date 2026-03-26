package com.app.ecarepro.core.domain.model.menu

data class MenuCategory(
    val id: Int,
    val title: String,
    val iconUrl: String?,
    val menuItems: List<MenuItem>,
)
