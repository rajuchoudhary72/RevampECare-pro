package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.globalsearch.NetworkSearchStaff
import com.app.ecarepro.core.network.model.globalsearch.NetworkSearchStudent

interface GlobalSearchRemoteDataSource {
    suspend fun getStudents(): List<NetworkSearchStudent>
    suspend fun getStaff(): List<NetworkSearchStaff>
}
