package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.data.mapper.asEntity
import com.app.ecarepro.core.data.mapper.toDomainModel
import com.app.ecarepro.core.database.dao.SchoolDao
import com.app.ecarepro.core.domain.exception.InvalidSchoolCodeException
import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.OnboardingItem
import com.app.ecarepro.core.domain.model.School
import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.network.SchoolRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import com.app.ecarepro.core.domain.model.User
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class SchoolRepositoryImpl @Inject constructor(
    private val schoolRemoteDataSource: SchoolRemoteDataSource,
    private val schoolDao: SchoolDao,
) : SchoolRepository {
    override suspend fun getOnboardingData(): List<OnboardingItem> {
        return schoolRemoteDataSource.getOnboardingData().map { it.toDomainModel() }
    }

    override suspend fun getSchoolDetails(schoolCode: String): Flow<Result<String>> {
        return asResultFlow {
            schoolRemoteDataSource.getSchoolDetails(schoolCode).let { schoolDetails ->
                schoolDao.insertSchool(schoolDetails.asEntity())
                schoolCode
            }

        }.map { result ->
            result.onFailure { exception ->
                if (exception is HttpException && exception.code() == 400) {
                    return@map Result.failure(InvalidSchoolCodeException())
                }
            }
            result
        }
    }


    override suspend fun getSchools(): Flow<Result<List<School>>> {
        return asResultFlow {
            schoolRemoteDataSource.getSchools()
        }.map { result ->
            result.map { schools ->
                schools.map { school -> school.toDomainModel() }
            }
        }
    }

    override suspend fun getSchoolDetail(schoolCode: String): Flow<SchoolDetail> {
        return schoolDao.getSchool(schoolCode).map { it.toDomainModel() }
    }
}
