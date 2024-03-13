package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.AppLayoutDto
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getAppLayout(): Flow<Result<AppLayoutDto>>
}