package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.menu.MenuCategory
import com.app.ecarepro.core.domain.repository.MenuRepository
import com.app.ecarepro.core.network.MenuRemoteDataSource
import com.app.ecarepro.core.network.model.menu.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MenuRepositoryImpl @Inject constructor(
    private val dataSource: MenuRemoteDataSource,
) : MenuRepository {
    override fun fetchMenu(language: String): Flow<Result<List<MenuCategory>>> = asResultFlow {
        dataSource.fetchMenu(language).map { it.toDomainModel() }
    }
}
