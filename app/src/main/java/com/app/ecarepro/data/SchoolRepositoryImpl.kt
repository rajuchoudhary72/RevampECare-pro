package com.app.ecarepro.data

import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkCircularDetails
import com.app.ecarepro.data.network.model.NetworkNoticDetails
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.asExternalModel
import com.app.ecarepro.data.network.service.SchoolService
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.School
import com.app.ecarepro.model.Slide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SchoolRepositoryImpl @Inject constructor(
    private val schoolService: SchoolService,
    private val userDataStore: UserDataStore
) : SchoolRepository {
    override suspend fun fetchWalkThroughData() {
        schoolService.getWalkThroughData().slides.map { it.asExternalModel() }
            .also { userDataStore.saveSlides(it) }
    }

    override fun getOnboardingSlides(): Flow<List<Slide>> {
        return userDataStore.getSlides()
    }

    override fun validateSchoolCode(schoolCode: String): Flow<NetworkSchool?> {
        return flow {
            try {
                val response = schoolService.validateSchoolCode(schoolCode)
                userDataStore.saveSchoolData(response)
                emit(response)
            } catch (e: Exception) {
                emit(null)
            }
        }
    }

    override suspend fun getSchools(): List<School> {
        return schoolService.getSchools().list.map { it.asExternalModel() }
    }

    override suspend fun getNotice(pg: Int,classID: Int): NetworkNotice {
        return schoolService.getNotices(pg, classID)

    }

    override suspend fun getCirculars(pg: Int, yrID: Int,title :String): NetworkCircular {
         return schoolService.getCirculars(pg, yrID,title)
    }

    override suspend fun getNoticeDTL(ntID: Int, iD: Int): NetworkNoticDetails {
        return schoolService.getNoticeDTL(ntID, iD)
    }

    override suspend fun getCircularDTL(cirID: Int, iD: Int): NetworkCircularDetails {
        return schoolService.getCircularDTL(cirID, iD)
    }

}