package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkCircularDetails
import com.app.ecarepro.data.network.model.NetworkNoticDetails
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.model.School
import com.app.ecarepro.model.Slide
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    suspend fun fetchWalkThroughData()
    fun getOnboardingSlides(): Flow<List<Slide>>
    fun validateSchoolCode(schoolCode: String): Flow<NetworkSchool?>
    suspend fun getSchools(): List<School>
    suspend fun getNotice(pg: Int,classID: Int): NetworkNotice
    suspend fun getCirculars(pg: Int,yrID: Int,title :String): NetworkCircular
    suspend fun getNoticeDTL( ntID: Int, iD: Int ): NetworkNoticDetails
    suspend fun getCircularDTL( cirID: Int, iD: Int ): NetworkCircularDetails
}