package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.admin.NetworkGetSyllabuses
import com.app.ecarepro.core.network.model.admin.NetworkSyllabus

interface AdminRemoteDataSource {
    suspend fun getSyllabus(): List<NetworkSyllabus>
    suspend fun deleteSyllabus(syllabusId: String): NetworkGetSyllabuses
}