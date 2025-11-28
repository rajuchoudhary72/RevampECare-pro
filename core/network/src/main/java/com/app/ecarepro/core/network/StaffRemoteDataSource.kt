package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.staff.NetworkClass
import com.app.ecarepro.core.network.model.staff.NetworkSection
import com.app.ecarepro.core.network.model.staff.NetworkSubject

interface StaffRemoteDataSource {
    suspend fun getClasses(): List<NetworkClass>
    suspend fun getSections(classStd: String): List<NetworkSection>
    suspend fun getSubjects(classStd: String): List<NetworkSubject>
}