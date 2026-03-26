package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.menu.MenuCategory
import kotlinx.coroutines.flow.Flow

interface MenuRepository {
    fun fetchMenu(language: String): Flow<Result<List<MenuCategory>>>
}
