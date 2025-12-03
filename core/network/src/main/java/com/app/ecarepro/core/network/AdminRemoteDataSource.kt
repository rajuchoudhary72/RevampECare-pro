package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.admin.NetworkSaveSyllabus
import com.app.ecarepro.core.network.model.admin.NetworkSyllabus

interface AdminRemoteDataSource {
    suspend fun getSyllabus(): List<NetworkSyllabus>
    suspend fun deleteSyllabus(syllabusId: String): Boolean
    suspend fun saveSyllabus(syllabus: NetworkSaveSyllabus): String

}