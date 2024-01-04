package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.NetworkNotice
 import com.app.ecarepro.model.Notice
import com.app.ecarepro.model.School
import com.app.ecarepro.model.Slide
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    suspend fun fetchWalkThroughData()
    fun getOnboardingSlides(): Flow<List<Slide>>
    fun validateSchoolCode(schoolCode: String): Flow<Boolean>
    suspend fun getSchools(): List<School>
    suspend fun getNotice(pg: Int,classID: Int): List<Notice>
}