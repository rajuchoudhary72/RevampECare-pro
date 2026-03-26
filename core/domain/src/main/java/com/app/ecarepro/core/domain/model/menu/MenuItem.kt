package com.app.ecarepro.core.domain.model.menu

data class MenuItem(
    val id: Int,
    val parentMenuID: Int?,
    val title: String,
    val url: String?,
    val iconUrl: String?,
    val hasChildren: Boolean,
    val isNavigable: Boolean,
    val children: List<MenuItem>,
)
